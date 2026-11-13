package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.handler.GlobalExceptionHandler;
import dev.vdrenkov.biblium.service.OrderService;
import dev.vdrenkov.biblium.util.factory.OrderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static dev.vdrenkov.biblium.util.constant.TestConstants.CHOICE_STRING;
import static dev.vdrenkov.biblium.util.constant.TestConstants.DATE_STRING;
import static dev.vdrenkov.biblium.util.constant.TestConstants.EMAIL;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static dev.vdrenkov.biblium.util.constant.TestConstants.NAME;
import static dev.vdrenkov.biblium.util.constant.TestConstants.PERIOD_STRING;
import static dev.vdrenkov.biblium.util.constant.TestConstants.PHONE_NUMBER;
import static dev.vdrenkov.biblium.util.constant.TestConstants.SURNAME;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    private static final String URI = "/orders";
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
    void testAddOrder_noExceptions_success() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(OrderFactory.getDefaultOrderRequest());
        when(orderService.addOrder(any())).thenReturn(OrderFactory.getDefaultOrder());

        mockMvc
            .perform(post(URI).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", URI + "/" + ID));
    }

    @Test
    void testGetAllOrders_singleOrder_success() throws Exception {
        when(orderService.getAllOrdersDto()).thenReturn(OrderFactory.getDefaultOrdersDtoList());

        mockMvc
            .perform(get(URI))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].clientDto.name").value(NAME))
            .andExpect(jsonPath("$[0].clientDto.surname").value(SURNAME))
            .andExpect(jsonPath("$[0].clientDto.phoneNumber").value(PHONE_NUMBER))
            .andExpect(jsonPath("$[0].clientDto.email").value(EMAIL))
            .andExpect(jsonPath("$[0].booksNames", hasSize(1)))
            .andExpect(jsonPath("$[0].issueDate").value(DATE_STRING))
            .andExpect(jsonPath("$[0].dueDate").value(DATE_STRING));
    }

    @Test
    void testGetAllOrders_emptyList_success() throws Exception {
        mockMvc
            .perform(get(URI))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test
    void testGetALlOrdersByClient_singleOrder_success() throws Exception {
        when(orderService.getAllOrdersDtoByClient(anyInt())).thenReturn(OrderFactory.getDefaultOrdersDtoList());

        mockMvc
            .perform(get("/clients/" + ID + URI))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].clientDto.name").value(NAME))
            .andExpect(jsonPath("$[0].clientDto.surname").value(SURNAME))
            .andExpect(jsonPath("$[0].clientDto.phoneNumber").value(PHONE_NUMBER))
            .andExpect(jsonPath("$[0].clientDto.email").value(EMAIL))
            .andExpect(jsonPath("$[0].booksNames", hasSize(1)))
            .andExpect(jsonPath("$[0].issueDate").value(DATE_STRING))
            .andExpect(jsonPath("$[0].dueDate").value(DATE_STRING));
    }

    @Test
    void testGetAllOrdersByClient_emptyList_success() throws Exception {
        mockMvc
            .perform(get("/clients/" + ID + URI))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test
    void testGetALlOrdersByDate_singleOrder_success() throws Exception {
        when(orderService.getAllOrdersDtoByDate(anyInt(), anyString())).thenReturn(
            OrderFactory.getDefaultOrdersDtoList());

        mockMvc
            .perform(get(URI).queryParam("choice", CHOICE_STRING).queryParam("date", DATE_STRING))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].clientDto.name").value(NAME))
            .andExpect(jsonPath("$[0].clientDto.surname").value(SURNAME))
            .andExpect(jsonPath("$[0].clientDto.phoneNumber").value(PHONE_NUMBER))
            .andExpect(jsonPath("$[0].clientDto.email").value(EMAIL))
            .andExpect(jsonPath("$[0].booksNames", hasSize(1)))
            .andExpect(jsonPath("$[0].issueDate").value(DATE_STRING))
            .andExpect(jsonPath("$[0].dueDate").value(DATE_STRING));
    }

    @Test
    void testGetAllOrdersByDate_emptyList_success() throws Exception {
        when(orderService.getAllOrdersDtoByDate(anyInt(), anyString())).thenReturn(java.util.Collections.emptyList());

        mockMvc
            .perform(get(URI).queryParam("choice", CHOICE_STRING).queryParam("date", DATE_STRING))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test
    void testGetAllOrdersByDate_invalidChoice_badRequest() throws Exception {
        when(orderService.getAllOrdersDtoByDate(anyInt(), anyString())).thenThrow(
            new IllegalArgumentException("Choice must be between 1 and 6"));

        mockMvc
            .perform(get(URI).queryParam("choice", "99").queryParam("date", DATE_STRING))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrderById_noExceptions_success() throws Exception {
        when(orderService.getOrderDtoById(anyInt())).thenReturn(OrderFactory.getDefaultOrderDto());

        mockMvc
            .perform(get(URI + "/" + ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.clientDto.name").value(NAME))
            .andExpect(jsonPath("$.clientDto.surname").value(SURNAME))
            .andExpect(jsonPath("$.clientDto.phoneNumber").value(PHONE_NUMBER))
            .andExpect(jsonPath("$.clientDto.email").value(EMAIL))
            .andExpect(jsonPath("$.booksNames", hasSize(1)))
            .andExpect(jsonPath("$.issueDate").value(DATE_STRING))
            .andExpect(jsonPath("$.dueDate").value(DATE_STRING));
    }

    @Test
    void testExtendOrderDueByDate_requestedResponse_success() throws Exception {
        when(orderService.extendOrderDueByDate(anyInt(), anyInt(), anyInt())).thenReturn(
            OrderFactory.getDefaultOrderDto());

        mockMvc
            .perform(put(URI + "/" + ID)
                .queryParam("choice", CHOICE_STRING)
                .queryParam("period", PERIOD_STRING)
                .queryParam("returnOld", "true"))
            .andExpect(status().isOk());
    }

    @Test
    void testExtendOrderDueByDate_noResponse_success() throws Exception {
        when(orderService.extendOrderDueByDate(anyInt(), anyInt(), anyInt())).thenReturn(
            OrderFactory.getDefaultOrderDto());

        mockMvc
            .perform(put(URI + "/" + ID).queryParam("choice", CHOICE_STRING).queryParam("period", PERIOD_STRING))
            .andExpect(status().isNoContent());
    }

    @Test
    void testExtendOrderDueByDate_invalidChoice_badRequest() throws Exception {
        when(orderService.extendOrderDueByDate(anyInt(), anyInt(), anyInt())).thenThrow(
            new IllegalArgumentException("Choice must be between 1 and 3"));

        mockMvc
            .perform(put(URI + "/" + ID).queryParam("choice", "99").queryParam("period", PERIOD_STRING))
            .andExpect(status().isBadRequest());
    }
}

