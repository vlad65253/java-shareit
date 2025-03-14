package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.interfaces.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(HEADER_USER_ID) Long userId, @RequestBody CreateBookingDto createBookingDto) {
        return bookingService.addBooking(userId, createBookingDto);
    }
    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestParam boolean approved, @RequestHeader(HEADER_USER_ID) long userId, @PathVariable long bookingId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForBookerId(@RequestHeader(HEADER_USER_ID) Long userId, @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsForBookerIdAndState(userId, state);
    }
    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwnerId(@RequestHeader(HEADER_USER_ID) Long userId, @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByOwnerIdAndState(userId, state);
    }
    @GetMapping("/{bookingId}")
    public BookingDto getBookingsByBookerOrOwnerItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingsByOwnerAndBooker(userId, bookingId);
    }

}
