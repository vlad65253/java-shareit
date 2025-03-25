package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private CreateBookingDto createBookingDto;
    private BookingDto bookingDto;
    private Long userId;
    private Long itemId;
    private Long bookingId;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создание пользователя
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        // Создание вещи
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
        itemId = item.getId();

        // Создание DTO для бронирования
        createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(LocalDateTime.now().plusHours(1));
        createBookingDto.setEnd(LocalDateTime.now().plusHours(2));
        createBookingDto.setItemId(itemId);

        // Создание DTO для бронирования
        bookingDto = BookingDto.builder().build();
        bookingDto.setId(1L);
        bookingDto.setStart(createBookingDto.getStart());
        bookingDto.setEnd(createBookingDto.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(item));
        bookingDto.setBooker(UserMapper.toUserDto(user));
        bookingDto.setStatus(Status.WAITING);

        bookingId = bookingDto.getId();
    }

    @Test
    void testCreateBooking() {
        BookingDto bookingDto = bookingService.addBooking(userId, createBookingDto);
        assertNotNull(bookingDto);
        assertEquals(createBookingDto.getStart(), bookingDto.getStart());
        assertEquals(createBookingDto.getEnd(), bookingDto.getEnd());
        assertEquals(itemId, bookingDto.getItem().getId());
        assertEquals(userId, bookingDto.getBooker().getId());
    }

    @Test
    void testGetBookingById() {
        BookingDto createdBooking = bookingService.addBooking(userId, createBookingDto);
        BookingDto retrievedBooking = bookingService.getBookingsByOwnerAndBooker(userId, createdBooking.getId());
        assertNotNull(retrievedBooking);
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
    }

    @Test
    void testGetAllUsersBookingByStatusAll() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
    }

    @Test
    void testGetAllUsersBookingByStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(currentBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.CURRENT);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllUsersBookingByStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();

        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(pastBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.PAST);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllUsersBookingByStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(futureBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.FUTURE);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllUsersBookingByStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.patchBooking(pastBookingId, true, userId);
        bookingService.patchBooking(currentBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.WAITING);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
        assertEquals(futureBookingDto.getItem().getId(), itemId);
        assertEquals(futureBookingDto.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllUsersBookingByStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(currentBookingId, false, userId);

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
        assertEquals(17, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testUpdateBookingStatus() {
        BookingDto createdBooking = bookingService.addBooking(userId, createBookingDto);
        BookingDto updatedBooking = bookingService.patchBooking(createdBooking.getId(), true, userId);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testGetAllBookingForUserItemsByStatus() {
        bookingService.addBooking(userId, createBookingDto);
        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(currentBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsForBookerIdAndState(user2Id, State.CURRENT);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllBookingForUserItemsByStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(pastBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.PAST);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllBookingForUserItemsByStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(futureBookingId, true, userId);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.FUTURE);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllBookingForUserItemsByStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.patchBooking(currentBookingId, true, userId);
        bookingService.patchBooking(pastBookingId, true, userId);


        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.WAITING);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
        assertEquals(futureBookingDto.getItem().getId(), itemId);
        assertEquals(futureBookingDto.getBooker().getId(), user2Id);
    }

    @Test
    void testGetAllBookingForUserItemsByStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        CreateBookingDto pastBooking = new CreateBookingDto();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.addBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        CreateBookingDto currentBooking = new CreateBookingDto();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.addBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        CreateBookingDto futureBooking = new CreateBookingDto();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.addBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        BookingDto patchBooking = bookingService.patchBooking(currentBookingId, false, userId);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerIdAndState(userId, State.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
        assertEquals(4, bookings.getFirst().getId());
        assertEquals(patchBooking.getItem().getId(), itemId);
        assertEquals(patchBooking.getBooker().getId(), user2Id);
    }

    @Test
    void testGetByIdWithInvalidUserId() {
        BookingDto createdBooking = bookingService.addBooking(userId, createBookingDto);
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwnerAndBooker(createdBooking.getId(), 999L));
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(userId, createBookingDto));
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(999L, createBookingDto));
    }

    @Test
    void testCreateBookingWithNonExistentItem() {
        createBookingDto.setItemId(999L);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(userId, createBookingDto));
    }
}