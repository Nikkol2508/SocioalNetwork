package application.service;

import application.dao.DaoPerson;
import application.models.dto.MessageRequestDto;
import application.models.Person;
import application.models.dto.PersonDto;
import application.models.requests.AuthDtoRequest;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DaoPerson daoPerson;

    public GeneralResponse<PersonDto> getAuth(AuthDtoRequest authDtoRequest, String token) {
            Person person = daoPerson.getByEmail(authDtoRequest.getEmail());
            PersonDto personDto = PersonDto.fromPerson(person);
            personDto.setToken(token);
            return new GeneralResponse<>(personDto);
    }

    public GeneralResponse<MessageRequestDto> getLogout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new GeneralResponse<>(new MessageRequestDto("ok"));
    }

}
