package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("update Booking b set b.status = ?2 where b.id = ?1")
    void updateBookingStatusById(Long id, String status);

    List<Booking> findBookingByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long bookerId, String status,
                                                                 Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 "
            + "and current_timestamp between b.start and b.end "
            + "order by b.start DESC")
    List<Booking> findCurrentBookingByBookerId(Long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 "
            + "and current_timestamp > b.end "
            + "order by b.start DESC")
    List<Booking> findPastBookingByBookerId(Long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 "
            + "and current_timestamp < b.start "
            + "order by b.start DESC")
    List<Booking> findFutureBookingByBookerId(Long bookerId, Pageable pageable);

    @Query("select (count(b) > 0) from Booking b where b.booker.id = ?1 and b.status = ?2 and b.end < current_timestamp")
    boolean existsBookingByBookerIdAndStatus(Long bookerId, String status);

    List<Booking> findBookingsByItemId(Long itemId);

    List<Booking> findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, String status,
                                                                      Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId,
                                                                          LocalDateTime start, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId,
                                                                         LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 "
            + "and current_timestamp between b.start and b.end "
            + "order by b.start DESC")
    List<Booking> findCurrentBookingByOwnerId(Long ownerId, Pageable pageable);
}

