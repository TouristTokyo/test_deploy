package ru.vsu.cs.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.api.models.Channel;
import ru.vsu.cs.api.models.Member;
import ru.vsu.cs.api.models.Role;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.repositories.MemberRepository;
import ru.vsu.cs.api.utils.exceptions.MemberException;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getMembersByUser(User user) {
        return memberRepository.findByUser(user);
    }

    public List<Member> getMembersByChannel(Channel channel) {
        return memberRepository.findByChannel(channel);
    }

    public Member getMemberByUserAndChannel(User user, Channel channel) {
        Member member = memberRepository.findByUserAndChannel(user, channel).orElse(null);
        if (member == null) {
            log.warn("Not found member with name (" + user.getName() + ") for channel with name ( "
                    + channel.getName() + ")");
            throw new MemberException("Не существует участника (" + user.getName() + ") в канале ( "
                    + channel.getName() + ")");
        }

        return member;
    }

    @Transactional
    public void save(Member member) {
        log.info(member.getUser().getName() + " join to " + member.getChannel().getName());
        memberRepository.save(member);
    }

    @Transactional
    public void delete(BigInteger id) {
        memberRepository.deleteById(id);
    }

    @Transactional
    public void updateRole(Member member, Role role) {
        member.setRole(role);
        memberRepository.save(member);

    }
}
