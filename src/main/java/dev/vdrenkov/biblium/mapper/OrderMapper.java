package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.OrderDto;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.entity.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static List<OrderDto> mapOrdersToOrdersDto(final List<Order> orders) {
        final List<OrderDto> ordersDto = new ArrayList<>();

        for (final Order order : orders) {
            ordersDto.add(mapOrderToOrderDto(order));
        }

        ordersDto.sort(Comparator.comparing(OrderDto::id));
        return ordersDto;
    }

    public static OrderDto mapOrderToOrderDto(final Order order) {
        final List<String> booksNames = new ArrayList<>();

        for (final Book book : order.getBooks()) {
            booksNames.add(book.getName());
        }

        return new OrderDto(order.getId(), ClientMapper.mapClientToClientDto(order.getClient()), booksNames,
            order.getIssueDate(), order.getDueDate());
    }
}
