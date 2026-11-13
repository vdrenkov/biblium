package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.OrderDto;
import dev.vdrenkov.biblium.entity.Order;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.repository.OrderRepository;
import dev.vdrenkov.biblium.request.OrderRequest;
import dev.vdrenkov.biblium.util.factory.BookFactory;
import dev.vdrenkov.biblium.util.factory.ClientFactory;
import dev.vdrenkov.biblium.util.factory.OrderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.vdrenkov.biblium.util.constant.TestConstants.CHOICE;
import static dev.vdrenkov.biblium.util.constant.TestConstants.DATE_STRING;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static dev.vdrenkov.biblium.util.constant.TestConstants.LOCAL_DATE;
import static dev.vdrenkov.biblium.util.constant.TestConstants.PERIOD;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private ClientService clientService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testAddOrder_success() {
        when(clientService.getClientById(anyInt())).thenReturn(ClientFactory.getDefaultClient());
        when(bookService.decreaseBookQuantity(anyInt())).thenReturn(BookFactory.getDefaultBook());
        when(orderRepository.save(any())).thenReturn(new Order());

        final Order order = orderService.addOrder(OrderFactory.getDefaultOrderRequest());

        assertNotNull(order);
    }

    @Test
    void testAddOrder_noBooksIds_throws() {
        final OrderRequest orderRequest = new OrderRequest(ID, Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(orderRequest));
    }

    @Test
    void testAddOrder_bookOutOfStock_throws() {
        when(clientService.getClientById(anyInt())).thenReturn(ClientFactory.getDefaultClient());
        when(bookService.decreaseBookQuantity(anyInt())).thenThrow(new IllegalStateException("Book is out of stock"));

        final OrderRequest orderRequest = OrderFactory.getDefaultOrderRequest();
        assertThrows(IllegalStateException.class, () -> orderService.addOrder(orderRequest));
    }

    @Test
    void testAddOrder_duplicateBookIds_throws() {
        final OrderRequest orderRequest = new OrderRequest(ID, List.of(ID, ID));
        assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(orderRequest));
    }

    @Test
    void addOrder_multipleBooks_locksStockInIdOrderWithoutAnEarlierRead() {
        final Book firstBook = BookFactory.getDefaultBook();
        final Book secondBook = BookFactory.getDefaultBook();
        secondBook.setId(2);
        when(clientService.getClientById(ID)).thenReturn(ClientFactory.getDefaultClient());
        when(bookService.decreaseBookQuantity(1)).thenReturn(firstBook);
        when(bookService.decreaseBookQuantity(2)).thenReturn(secondBook);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Order order = orderService.addOrder(new OrderRequest(ID, List.of(2, 1)));

        final var stockUpdates = inOrder(bookService);
        stockUpdates.verify(bookService).decreaseBookQuantity(1);
        stockUpdates.verify(bookService).decreaseBookQuantity(2);
        verify(bookService, never()).getBookById(anyInt());
        assertEquals(List.of(firstBook, secondBook), order.getBooks());
    }

    @Test
    void extendOrderDueByDate_nonPositivePeriod_rejectsWithoutReadingOrder() {
        assertThrows(IllegalArgumentException.class, () -> orderService.extendOrderDueByDate(ID, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> orderService.extendOrderDueByDate(ID, 1, -1));
        verify(orderRepository, never()).findById(anyInt());
    }

    @Test
    void extendOrderDueByDate_validPeriod_usesLockedOrderAndReturnsPreviousDate() {
        final Order order = OrderFactory.getDefaultOrder();
        final LocalDate originalDate = order.getDueDate();
        when(orderRepository.findByIdForUpdate(ID)).thenReturn(Optional.of(order));

        final OrderDto previous = orderService.extendOrderDueByDate(ID, 1, 7);

        assertEquals(originalDate, previous.dueDate());
        assertEquals(originalDate.plusDays(7), order.getDueDate());
        verify(orderRepository).findByIdForUpdate(ID);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(OrderFactory.getDefaultOrdersList());

        final List<Order> testOrders = orderService.getAllOrders();

        assertNotNull(testOrders);
    }

    @Test
    void testGetAllOrdersDto() {
        when(orderRepository.findAll()).thenReturn(OrderFactory.getDefaultOrdersList());

        final List<OrderDto> result = orderService.getAllOrdersDto();

        assertNotNull(result);
    }

    @Test
    void testGetAllOrdersByClient() {
        when(orderRepository.findByClientId(anyInt())).thenReturn(OrderFactory.getDefaultOrdersList());

        final List<Order> testOrders = orderService.getAllOrdersByClient(ID);

        assertNotNull(testOrders);
    }

    @Test
    void testGetAllOrdersDtoByClient() {
        when(orderRepository.findByClientId(anyInt())).thenReturn(OrderFactory.getDefaultOrdersList());

        final List<OrderDto> result = orderService.getAllOrdersDtoByClient(ID);

        assertNotNull(result);
    }

    @Test
    void testGetAllOrdersByDate() {
        when(orderRepository.findByIssueDate(any(LocalDate.class))).thenReturn(OrderFactory.getDefaultOrdersList());

        final List<Order> testOrders = orderService.getAllOrdersByDate(1, LOCAL_DATE);

        assertNotNull(testOrders);
    }

    @Test
    void testGetAllOrdersByDate_invalidChoice_throws() {
        assertThrows(IllegalArgumentException.class, () -> orderService.getAllOrdersByDate(99, LOCAL_DATE));
    }

    @Test
    void testGetAllOrdersDtoByDate() {
        when(orderRepository.findByIssueDateAfter(any(LocalDate.class))).thenReturn(
            OrderFactory.getDefaultOrdersList());

        final List<OrderDto> result = orderService.getAllOrdersDtoByDate(CHOICE, DATE_STRING);

        assertNotNull(result);
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(OrderFactory.getDefaultOrder()));

        final Order order = orderService.getOrderById(ID);

        assertNotNull(order);
    }

    @Test
    void testGetOrderDtoById() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(OrderFactory.getDefaultOrder()));

        final OrderDto orderDto = orderService.getOrderDtoById(ID);

        assertNotNull(orderDto);
    }

    @Test
    void testExtendOrderDueByDate_success() {
        when(orderRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.of(OrderFactory.getDefaultOrder()));
        when(orderRepository.save(any())).thenReturn(OrderFactory.getDefaultOrder());

        final OrderDto orderDto1 = orderService.extendOrderDueByDate(ID, 1, PERIOD);
        final OrderDto orderDto2 = orderService.extendOrderDueByDate(ID, 2, PERIOD);
        final OrderDto orderDto3 = orderService.extendOrderDueByDate(ID, 3, PERIOD);
        final boolean result = orderDto1 != null && orderDto2 != null && orderDto3 != null;

        assertTrue(result);
    }

    @Test
    void testExtendOrderDueByDate_invalidChoice_throws() {
        when(orderRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.of(OrderFactory.getDefaultOrder()));

        assertThrows(IllegalArgumentException.class, () -> orderService.extendOrderDueByDate(ID, 99, PERIOD));
    }
}

