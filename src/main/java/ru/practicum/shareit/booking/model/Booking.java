package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bookings")
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User booker;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
