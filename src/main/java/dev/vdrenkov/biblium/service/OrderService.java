package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.OrderDto;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.entity.Client;
import dev.vdrenkov.biblium.entity.Order;
import dev.vdrenkov.biblium.exception.OrderNotFoundException;
import dev.vdrenkov.biblium.mapper.OrderMapper;
import dev.vdrenkov.biblium.repository.OrderRepository;
import dev.vdrenkov.biblium.request.OrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class OrderService {

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
        final List<Book> books = orderRequest.getBooksIds()
            .stream()
            .sorted()
            .map(bookService::decreaseBookQuantity)
            .toList();
        final LocalDate issueDate = LocalDate.now();

        return orderRepository.save(new Order(client, books, issueDate, issueDate.plusMonths(1)));
    }

    public List<Order> getAllOrders() {
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
        return OrderMapper.mapOrdersToOrdersDto(getAllOrdersByDate(choice, LocalDate.parse(date)));
    }

    public Order getOrderById(final int id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderDtoById(final int id) {
        return OrderMapper.mapOrderToOrderDto(getOrderById(id));
    }

    @Transactional
    public OrderDto extendOrderDueByDate(final int orderId, final int choice, final int period) {

        if (period <= 0) {
            throw new IllegalArgumentException("Period must be a positive number");
        }

        final Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(OrderNotFoundException::new);
        final OrderDto oldOrder = OrderMapper.mapOrderToOrderDto(order);

        final LocalDate dueDate = order.getDueDate();

        order.setDueDate(switch (choice) {
            case 1 -> dueDate.plusDays(period);
            case 2 -> dueDate.plusWeeks(period);
            case 3 -> dueDate.plusMonths(period);
            default -> throw new IllegalArgumentException("Choice must be between 1 and 3");
        });
        orderRepository.save(order);
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
