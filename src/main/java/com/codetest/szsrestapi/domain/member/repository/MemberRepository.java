package com.codetest.szsrestapi.domain.member.repository;

import com.codetest.szsrestapi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

}
