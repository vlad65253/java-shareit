package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, CreateBookingDto createBookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с таким айди нет"));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен");
        }
        List<Booking> bookings = bookingRepository.findAllByIntersectingStartAndEnd(
                item.getId(), createBookingDto.getStart(), createBookingDto.getEnd());
        if (!bookings.isEmpty()) {
            throw new ValidationException("Невозможно забронировать");
        }
        Booking booking = BookingMapper.toBookingNew(createBookingDto, item, user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsForBookerIdAndState(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    userId, Status.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwnerIdAndState(long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(
                    userId, Status.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, Status.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto getBookingsByOwnerAndBooker(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Данное бронирование не найдено");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto patchBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId() != userId) {
            throw new ValidationException("Вы не вледелец этой вещи");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.CANCELED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}
