package dev.vdrenkov.biblium;

import dev.vdrenkov.biblium.jwt.JwtConstants;
import dev.vdrenkov.biblium.repository.BookRepository;
import dev.vdrenkov.biblium.repository.OrderRepository;
import dev.vdrenkov.biblium.repository.UserRepository;
import dev.vdrenkov.biblium.repository.UserRoleRepository;
import dev.vdrenkov.biblium.request.OrderRequest;
import dev.vdrenkov.biblium.request.AuthorRequest;
import dev.vdrenkov.biblium.request.BookRequest;
import dev.vdrenkov.biblium.request.ClientRequest;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import dev.vdrenkov.biblium.request.UserRequest;
import dev.vdrenkov.biblium.service.OrderService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "jwt.secret=integration-tests-use-a-dedicated-signing-secret-of-at-least-sixty-four-bytes",
    "jwt.cookie.secure=false",
    "spring.datasource.password=managed-by-testcontainers",
    "app.bootstrap.admin.username=integration_admin",
    "app.bootstrap.admin.password=IntegrationPassword123"
})
@AutoConfigureMockMvc
@Testcontainers
class BibliumIT {
    private static final String PASSWORD = "IntegrationPassword123";
    private static final AtomicLong PHONE_NUMBER = new AtomicLong(9000000000L);

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName.parse(
        "cgr.dev/chainguard/postgres@sha256:69ae93d5150b50f20b5fa5d13914e33f04aa54a30edecf936788af663ee4f867")
        .asCompatibleSubstituteFor("postgres"))
        .withCommand("-c", "fsync=off")
        .waitingFor(Wait.forListeningPort());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${local.server.port}")
    private int serverPort;

    @Test
    void server_cookieBasedAuthentication_preservesCsrfAcrossRequests() throws Exception {
        try (final var client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL))
            .build()) {
            final var request = java.net.http.HttpRequest.newBuilder(
                java.net.URI.create("http://localhost:" + serverPort + "/csrf")).build();
            final var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertTrue(objectMapper.readTree(response.body()).hasNonNull("token"));
            assertTrue(response.headers().allValues("Set-Cookie").stream().anyMatch(header -> header.startsWith("XSRF-TOKEN=")));
            final String token = objectMapper.readTree(response.body()).path("token").asString();
            final var login = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://localhost:" + serverPort + "/login"))
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", token)
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                    new UserRequest("integration_admin", PASSWORD))))
                .build();
            assertEquals(200, client.send(login, java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode());
            for (final String name : List.of("First", "Second")) {
                final String surname = "Author" + UUID.randomUUID().toString().replaceAll("[^a-f]", "");
                final var author = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://localhost:" + serverPort + "/authors"))
                    .header("Content-Type", "application/json")
                    .header("X-XSRF-TOKEN", token)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                        new AuthorRequest(name, surname))))
                    .build();
                assertEquals(201, client.send(author, java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode());
            }
        }
    }

    @Test
    void createUser_administrator_createsLibrarianWithoutReplacingAuthentication() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final String username = "staff_" + UUID.randomUUID().toString().replace("-", "");
        final int roleId = userRoleRepository.findUserRoleByRole("LIBRARIAN").orElseThrow().getId();

        mockMvc
            .perform(authenticate(post("/users"), admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new CreateUserRequest(username, PASSWORD, List.of(roleId)))))
            .andExpect(status().isCreated())
            .andExpect(cookie().doesNotExist(JwtConstants.JWT_COOKIE_NAME));

        assertTrue(userRepository.existsByUsername(username));
        assertFalse(userRepository.findByUsername(username).orElseThrow().getPassword().equals(PASSWORD));
        final Session librarian = login(username, PASSWORD);
        mockMvc.perform(authenticate(get("/books"), librarian)).andExpect(status().isOk());
        mockMvc.perform(authenticate(get("/users"), librarian)).andExpect(status().isForbidden());
        mockMvc
            .perform(authenticate(post("/users"), librarian)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isForbidden());
        mockMvc.perform(authenticate(get("/users"), admin)).andExpect(status().isOk());
    }

    @Test
    void login_realCsrfTokenAndCookie_enforcesAuthenticationAndLogout() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        mockMvc.perform(get("/books")).andExpect(status().isForbidden());
        mockMvc
            .perform(post("/users").cookie(admin.authentication()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
        mockMvc
            .perform(get("/books").cookie(new Cookie(JwtConstants.JWT_COOKIE_NAME, "invalid-token")))
            .andExpect(status().isForbidden());
        mockMvc
            .perform(authenticate(post("/logout"), admin))
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge(JwtConstants.JWT_COOKIE_NAME, 0));
        mockMvc.perform(get("/books")).andExpect(status().isForbidden());
    }

    @Test
    void catalogueAndLending_validRequests_persistsAndFiltersRecords() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final Fixture fixture = createFixture(admin, 2);
        final int orderId = create(admin, "/orders", new OrderRequest(fixture.clientId(), List.of(fixture.bookId())));
        assertEquals(1, bookRepository.findById(fixture.bookId()).orElseThrow().getQuantity());

        mockMvc
            .perform(authenticate(get("/orders/" + orderId), admin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientDto.id").value(fixture.clientId()))
            .andExpect(jsonPath("$.booksNames[0]").value(fixture.bookName()))
            .andExpect(jsonPath("$.issueDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusMonths(1).toString()));
        mockMvc
            .perform(authenticate(get("/clients/" + fixture.clientId() + "/orders"), admin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(orderId));
        mockMvc
            .perform(authenticate(put("/orders/" + orderId), admin)
                .queryParam("choice", "1")
                .queryParam("period", "7")
                .queryParam("returnOld", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusMonths(1).toString()));
        mockMvc
            .perform(authenticate(get("/orders/" + orderId), admin))
            .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusMonths(1).plusDays(7).toString()));
        mockMvc
            .perform(authenticate(get("/orders"), admin).queryParam("choice", "1").queryParam("date", "2000-01-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addOrder_unavailableSecondBook_rollsBackAllStockChanges() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final Fixture first = createFixture(admin, 1);
        final Fixture second = createFixture(admin, 1);
        create(admin, "/orders", new OrderRequest(second.clientId(), List.of(second.bookId())));
        final long initialOrders = orderRepository.count();

        assertThrows(IllegalStateException.class,
            () -> orderService.addOrder(new OrderRequest(first.clientId(), List.of(first.bookId(), second.bookId()))));

        assertEquals(1, bookRepository.findById(first.bookId()).orElseThrow().getQuantity());
        assertEquals(0, bookRepository.findById(second.bookId()).orElseThrow().getQuantity());
        assertEquals(initialOrders, orderRepository.count());
    }

    @Test
    void addOrder_concurrentRequestsForLastCopy_createsExactlyOneOrder() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final Fixture fixture = createFixture(admin, 1);
        final long initialOrders = orderRepository.count();
        final CountDownLatch start = new CountDownLatch(1);

        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            final var requests = java.util.stream.IntStream.range(0, 8)
                .mapToObj(_ -> executor.submit(() -> {
                    assertTrue(start.await(10, TimeUnit.SECONDS));
                    try {
                        orderService.addOrder(new OrderRequest(fixture.clientId(), List.of(fixture.bookId())));
                        return true;
                    } catch (final IllegalStateException exception) {
                        return false;
                    }
                }))
                .toList();
            start.countDown();
            int successfulOrders = 0;
            for (final var request : requests) {
                if (request.get(20, TimeUnit.SECONDS)) {
                    successfulOrders++;
                }
            }
            assertEquals(1, successfulOrders);
        }

        assertEquals(initialOrders + 1, orderRepository.count());
        assertEquals(0, bookRepository.findById(fixture.bookId()).orElseThrow().getQuantity());
    }

    @Test
    void extendOrder_concurrentRequests_preservesEveryExtension() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final Fixture fixture = createFixture(admin, 1);
        final int orderId = create(admin, "/orders", new OrderRequest(fixture.clientId(), List.of(fixture.bookId())));
        final LocalDate originalDate = orderRepository.findById(orderId).orElseThrow().getDueDate();
        final CountDownLatch start = new CountDownLatch(1);

        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            final var extensions = java.util.stream.IntStream.range(0, 4)
                .mapToObj(_ -> executor.submit(() -> {
                    assertTrue(start.await(10, TimeUnit.SECONDS));
                    return orderService.extendOrderDueByDate(orderId, 1, 1);
                }))
                .toList();
            start.countDown();
            for (final var extension : extensions) {
                assertNotNull(extension.get(20, TimeUnit.SECONDS));
            }
        }

        assertEquals(originalDate.plusDays(4), orderRepository.findById(orderId).orElseThrow().getDueDate());
    }

    @Test
    void requests_invalidAndMissingResources_returnClientErrors() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        mockMvc.perform(authenticate(get("/books/2147483647"), admin)).andExpect(status().isNotFound());
        mockMvc.perform(authenticate(post("/register"), admin)).andExpect(status().isNotFound());
        mockMvc.perform(authenticate(post("/admins/register"), admin)).andExpect(status().isNotFound());
        mockMvc
            .perform(authenticate(post("/orders"), admin).contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientId\":1,\"booksIds\":[]}"))
            .andExpect(status().isBadRequest());
        mockMvc
            .perform(authenticate(get("/orders"), admin).queryParam("choice", "1").queryParam("date", "invalid"))
            .andExpect(status().isBadRequest());
        mockMvc
            .perform(authenticate(put("/orders/1"), admin).queryParam("choice", "1").queryParam("period", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteClient_administrator_preservesRequestedResponse() throws Exception {
        final Session admin = login("integration_admin", PASSWORD);
        final Fixture fixture = createFixture(admin, 1);
        mockMvc
            .perform(authenticate(delete("/clients/" + fixture.clientId()), admin).queryParam("returnOld", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(fixture.clientId()));
        mockMvc.perform(authenticate(get("/clients/" + fixture.clientId()), admin)).andExpect(status().isNotFound());
    }

    private Session login(final String username, final String password) throws Exception {
        final MvcResult csrfResult = mockMvc.perform(get("/csrf")).andExpect(status().isOk()).andReturn();
        final Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        assertNotNull(csrfCookie);
        final MvcResult loginResult = mockMvc
            .perform(post("/login")
                .cookie(csrfCookie)
                .header("X-XSRF-TOKEN", csrfCookie.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserRequest(username, password))))
            .andExpect(status().isOk())
            .andExpect(cookie().httpOnly(JwtConstants.JWT_COOKIE_NAME, true))
            .andReturn();
        final Cookie authentication = loginResult.getResponse().getCookie(JwtConstants.JWT_COOKIE_NAME);
        assertNotNull(authentication);
        return new Session(authentication, csrfCookie);
    }

    private MockHttpServletRequestBuilder authenticate(final MockHttpServletRequestBuilder request,
        final Session session) {
        return request.cookie(session.authentication(), session.csrf()).header("X-XSRF-TOKEN", session.csrf().getValue());
    }

    private int create(final Session session, final String path, final Object body) throws Exception {
        final MvcResult result = mockMvc
            .perform(authenticate(post(path), session).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andReturn();
        final String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    private Fixture createFixture(final Session session, final int quantity) throws Exception {
        final String identifier = UUID.randomUUID().toString().replace("-", "");
        final String surname = "Author" + identifier.replaceAll("[^a-f]", "");
        final int authorId = create(session, "/authors", new AuthorRequest("Test", surname));
        final String bookName = "Library " + identifier;
        final int bookId = create(session, "/books",
            new BookRequest(bookName, LocalDate.of(2020, 1, 1), authorId, quantity));
        final int clientId = create(session, "/clients", new ClientRequest("Test", "Reader",
            Long.toString(PHONE_NUMBER.incrementAndGet()), identifier + "@example.test"));
        return new Fixture(clientId, bookId, bookName);
    }

    private record Session(Cookie authentication, Cookie csrf) {
    }

    private record Fixture(int clientId, int bookId, String bookName) {
    }
}
