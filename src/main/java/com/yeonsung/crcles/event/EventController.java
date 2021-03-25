package com.yeonsung.crcles.event;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.CurrentAccount;
import com.yeonsung.crcles.club.Club;
import com.yeonsung.crcles.club.ClubRepository;
import com.yeonsung.crcles.club.ClubService;
import com.yeonsung.crcles.event.form.EventForm;
import com.yeonsung.crcles.event.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/club/{path}")
@RequiredArgsConstructor
public class EventController {

    private final ClubService clubService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    /*
    * 모임생성
    * */
    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        model.addAttribute("club",club);
        model.addAttribute("account",account);
        model.addAttribute("eventForm",new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute("club",club);
            model.addAttribute("account",account);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

    /*
    * 동아리 모임조회
    * 동아리 모임목록조회
    * */
    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(clubService.getClub(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewClubEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);

        List<Event> events = eventRepository.findByClubOrderByStartDateTime(club);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "club/events";
    }

}
