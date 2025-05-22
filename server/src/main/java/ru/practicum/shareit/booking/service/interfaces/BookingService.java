package ru.practicum.shareit.booking.service.interfaces;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, CreateBookingDto createdBookingDto);

    List<BookingDto> getBookingsForBookerIdAndState(Long userId, State state);


    List<BookingDto> getBookingsByOwnerIdAndState(long userId, State state);

    BookingDto getBookingsByOwnerAndBooker(long userId, long bookingId);

    BookingDto patchBooking(long bookingId, boolean approved, long userId);
}
