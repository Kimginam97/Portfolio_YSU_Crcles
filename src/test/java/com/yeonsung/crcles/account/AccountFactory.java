package com.yeonsung.crcles.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account dudurian = new Account();
        dudurian.setNickname(nickname);
        dudurian.setEmail(nickname + "@email.com");
        accountRepository.save(dudurian);
        return dudurian;
    }

}
