package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId")
    List<Conversation> findByParticipantUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Conversation c WHERE " +
           "SIZE(c.participants) = 2 AND " +
           "EXISTS (SELECT p FROM c.participants p WHERE p.user.id = :user1Id) AND " +
           "EXISTS (SELECT p FROM c.participants p WHERE p.user.id = :user2Id)")
    Optional<Conversation> findDirectConversation(@Param("user1Id") UUID user1Id,
                                                   @Param("user2Id") UUID user2Id);
}
