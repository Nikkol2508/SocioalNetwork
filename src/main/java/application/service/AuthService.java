package application.service;

import application.dao.DaoPerson;
import application.models.MessageRequestDto;
import application.models.Person;
import application.models.PersonDto;
import application.requests.AuthDtoRequest;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DaoPerson daoPerson;

    public GeneralResponse<PersonDto> getAuth(AuthDtoRequest authDtoRequest, String token) {
            Person person = daoPerson.getByEmail(authDtoRequest.getEmail());
            return new GeneralResponse<>(PersonDto.fromPerson(person, token));
    }


    public GeneralResponse<MessageRequestDto> getLogout() {
        return new GeneralResponse<>(new MessageRequestDto("ok"));
    }

}
