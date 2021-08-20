package application.service;

import application.dao.DaoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DialogsService {

    private final DaoMessage daoMessage;
}
