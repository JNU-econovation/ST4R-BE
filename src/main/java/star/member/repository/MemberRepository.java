package star.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.member.model.entity.Member;
import star.member.model.vo.Email;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(Email email);
    Optional<Member> findByEmail(Email email);
}
