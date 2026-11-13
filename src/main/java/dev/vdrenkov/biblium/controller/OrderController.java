package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.OrderDto;
import dev.vdrenkov.biblium.entity.Order;
import dev.vdrenkov.biblium.request.OrderRequest;
import dev.vdrenkov.biblium.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Validated
public class OrderController {

    private static final String URI = "/orders";

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(URI)
    public ResponseEntity<Void> addOrder(@RequestBody @Valid final OrderRequest orderRequest) {
        final Order order = orderService.addOrder(orderRequest);

        final URI location = UriComponentsBuilder.fromUriString("/orders/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(URI)
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersDto());
    }

    @GetMapping("/clients/{clientId}" + URI)
    public ResponseEntity<List<OrderDto>> getAllOrdersByClient(@PathVariable final int clientId) {
        return ResponseEntity.ok(orderService.getAllOrdersDtoByClient(clientId));
    }

    @GetMapping(value = URI, params = { "choice", "date" })
    public ResponseEntity<List<OrderDto>> getALlOrdersByDate(
        @RequestParam @Min(value = 1, message = "Choice must be between 1 and 6") @Max(value = 6,
            message = "Choice must be between 1 and 6") final int choice, @RequestParam final String date) {
        return ResponseEntity.ok(orderService.getAllOrdersDtoByDate(choice, date));
    }

    @GetMapping(URI + "/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable final int id) {
        return ResponseEntity.ok(orderService.getOrderDtoById(id));
    }

    @PutMapping(URI + "/{id}")
    public ResponseEntity<OrderDto> extendOrderDueByDate(@PathVariable final int id,
        @RequestParam @Min(value = 1, message = "Choice must be between 1 and 3") @Max(value = 3,
            message = "Choice must be between 1 and 3") final int choice,
        @RequestParam @Positive(message = "Period must be a positive number") final int period,
        @RequestParam(required = false) final Boolean returnOld) {

        final OrderDto oldOrder = orderService.extendOrderDueByDate(id, choice, period);

        if (Boolean.TRUE.equals(returnOld)) {
            return ResponseEntity.ok(oldOrder);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
