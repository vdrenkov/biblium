package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.OrderDto;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.entity.Client;
import dev.vdrenkov.biblium.entity.Order;
import dev.vdrenkov.biblium.exception.OrderNotFoundException;
import dev.vdrenkov.biblium.mapper.LocalDateMapper;
import dev.vdrenkov.biblium.mapper.OrderMapper;
import dev.vdrenkov.biblium.repository.OrderRepository;
import dev.vdrenkov.biblium.request.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final BookService bookService;
    private final ClientService clientService;
    private final OrderRepository orderRepository;

    public OrderService(final BookService bookService, final ClientService clientService,
        final OrderRepository orderRepository) {
        this.bookService = bookService;
        this.clientService = clientService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order addOrder(final OrderRequest orderRequest) {
        if (orderRequest.getBooksIds().isEmpty()) {
            throw new IllegalArgumentException("At least one book ID is required to create an order");
        }
        validateNoDuplicateBookIds(orderRequest.getBooksIds());

        final Client client = clientService.getClientById(orderRequest.getClientId());
        final List<Book> books = new ArrayList<>();

        for (final int bookId : orderRequest.getBooksIds()) {
            books.add(bookService.getBookById(bookId));
        }

        decreaseBooksQuantities(orderRequest.getBooksIds());

        final LocalDate issueDate = LocalDate.now();

        log.info("Trying to add a new order");
        return orderRepository.save(new Order(client, books, issueDate, issueDate.plusMonths(1)));
    }

    private void decreaseBooksQuantities(final List<Integer> booksIds) {
        for (final int bookId : booksIds) {
            bookService.decreaseBookQuantity(bookId);
        }
    }

    public List<Order> getAllOrders() {
        log.info("Trying to retrieve all orders");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersDto() {
        return OrderMapper.mapOrdersToOrdersDto(getAllOrders());
    }

    public List<Order> getAllOrdersByClient(final int clientId) {
        return orderRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersDtoByClient(final int clientId) {
        return OrderMapper.mapOrdersToOrdersDto(getAllOrdersByClient(clientId));
    }

    public List<Order> getAllOrdersByDate(final int choice, final LocalDate date) {
        validateOrderDateChoice(choice);
        return switch (choice) {
            case 1 -> orderRepository.findByIssueDate(date);
            case 2 -> orderRepository.findByIssueDateBefore(date);
            case 3 -> orderRepository.findByIssueDateAfter(date);
            case 4 -> orderRepository.findByDueDate(date);
            case 5 -> orderRepository.findByDueDateBefore(date);
            case 6 -> orderRepository.findByDueDateAfter(date);
            default -> List.of();
        };
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersDtoByDate(final int choice, final String date) {
        return OrderMapper.mapOrdersToOrdersDto(getAllOrdersByDate(choice, LocalDateMapper.mapStringToDate(date)));
    }

    public Order getOrderById(final int id) {
        log.info("Trying to find order with an ID {}", id);
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderDtoById(final int id) {
        return OrderMapper.mapOrderToOrderDto(getOrderById(id));
    }

    @Transactional
    public OrderDto extendOrderDueByDate(final int orderId, final int choice, final int period) {
        final Order order = getOrderById(orderId);
        final OrderDto oldOrder = OrderMapper.mapOrderToOrderDto(order);

        final LocalDate dueDate = order.getDueDate();

        switch (choice) {
            case 1:
                order.setDueDate(dueDate.plusDays(period));
                break;
            case 2:
                order.setDueDate(dueDate.plusWeeks(period));
                break;
            case 3:
                order.setDueDate(dueDate.plusMonths(period));
                break;
            default:
                throw new IllegalArgumentException("Choice must be between 1 and 3");
        }
        orderRepository.save(order);
        log.info("Order due date successfully updated");
        return oldOrder;
    }

    private static void validateNoDuplicateBookIds(final List<Integer> booksIds) {
        if (new HashSet<>(booksIds).size() != booksIds.size()) {
            throw new IllegalArgumentException("Duplicate book IDs are not allowed in a single order");
        }
    }

    private static void validateOrderDateChoice(final int choice) {
        if (choice < 1 || choice > 6) {
            throw new IllegalArgumentException("Choice must be between 1 and 6");
        }
    }
}
