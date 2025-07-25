package poly.edu.repository;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ✅ Lấy danh sách tất cả thông báo theo userId, mới nhất trước
    List<Notification> findByReceiver_IdOrderByCreatedDateDesc(Long receiverId);

    // ✅ Lấy danh sách thông báo chưa đọc
    List<Notification> findByReceiver_IdAndIsReadFalse(Long receiverId);

    // ✅ Đếm thông báo chưa đọc
    long countByReceiver_IdAndIsReadFalse(Long receiverId);

    // ✅ Đánh dấu tất cả đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :receiverId")
    void markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);
}
