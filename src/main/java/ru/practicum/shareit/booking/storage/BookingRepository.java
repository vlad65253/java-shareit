package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(Long userId, Status status, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(Long userId, Status status, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(Long userId, Status status, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(long userId, Status status, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(long userId, Status status, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(long userId, Status status, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItemIdAndBookerId(long itemId, long userId);
}
