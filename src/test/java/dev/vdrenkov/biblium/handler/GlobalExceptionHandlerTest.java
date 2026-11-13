package dev.vdrenkov.biblium.handler;

import dev.vdrenkov.biblium.controller.OrderController;
import dev.vdrenkov.biblium.exception.AuthorNotFoundException;
import dev.vdrenkov.biblium.exception.BookNotFoundException;
import dev.vdrenkov.biblium.exception.ClientNotFoundException;
import dev.vdrenkov.biblium.exception.OrderNotFoundException;
import dev.vdrenkov.biblium.request.OrderRequest;
import dev.vdrenkov.biblium.service.OrderService;
import dev.vdrenkov.biblium.util.factory.BookFactory;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.ObjectMapper;

import java.time.format.DateTimeParseException;

import static dev.vdrenkov.biblium.util.constant.TestConstants.CHOICE;
import static dev.vdrenkov.biblium.util.constant.TestConstants.CHOICE_STRING;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private static final String URI = "/orders";
    private static final String ROOT = "$";

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(orderController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void handleMethodArgumentNotValidException_onEndpointGetAllOrders_badRequest() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(new OrderRequest(0, BookFactory.getDefaultBooksIdsList()));

        mockMvc
            .perform(post(URI).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("The client ID must be a positive digit")));
    }

    @Test
    void testHandleNullPointerException_onEndpointGetAllOrders_internalServerError() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(NullPointerException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath(ROOT, is("Something went wrong")));
    }

    @Test
    void testHandleUnexpectedTypeException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(UnexpectedTypeException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Wrong data entered")));
    }

    @Test
    void testHandleHttpMessageNotReadableException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(HttpMessageNotReadableException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Incorrect data entered")));
    }

    @Test
    void testHandleHttpRequestMethodNotSupportedException_onEndpointGetAllOrders_methodNotAllowed() throws Exception {
        mockMvc
            .perform(put(URI))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath(ROOT, is("HTTP method not supported")));
    }

    @Test
    void testHandleIllegalArgumentException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(IllegalArgumentException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Illegal argument provided")));
    }

    @Test
    void testHandleMissingServletRequestParameterException_onEndpointGetAllOrders_badRequest() throws Exception {
        mockMvc
            .perform(put(URI + "/" + ID))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Invalid parameter provided")));
    }

    @Test
    void testHandleDateTimeParseException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDtoByDate(CHOICE, "mistake")).thenThrow(DateTimeParseException.class);

        mockMvc
            .perform(get(URI).queryParam("choice", CHOICE_STRING).queryParam("date", "mistake"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Invalid date provided")));
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(MethodArgumentTypeMismatchException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Invalid URL provided")));
    }

    @Test
    void testHandleDataIntegrityViolationException_onEndpointGetAllOrders_badRequest() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(DataIntegrityViolationException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ROOT, is("Violation of database rules!")));
    }

    @Test
    void testHandleAuthorNotFoundException_onEndpointGetAllOrders_notFound() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(AuthorNotFoundException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ROOT, is("No such author found in the database")));
    }

    @Test
    void testHandleBookNotFoundException_onEndpointGetAllOrders_notFound() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(BookNotFoundException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ROOT, is("No such book found in the database")));
    }

    @Test
    void testHandleClientNotFoundException_onEndpointGetAllOrders_notFound() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(ClientNotFoundException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ROOT, is("No such client found in the database")));
    }

    @Test
    void testHandleOrderNotFoundException_onEndpointGetAllOrders_notFound() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(OrderNotFoundException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ROOT, is("No such order found in the database")));
    }

    @Test
    void testHandleException_onEndpointGetAllOrders_internalServerError() throws Exception {
        when(orderService.getAllOrdersDto()).thenThrow(IllegalStateException.class);

        mockMvc
            .perform(get(URI))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath(ROOT, is("Something went wrong")));
    }
}

