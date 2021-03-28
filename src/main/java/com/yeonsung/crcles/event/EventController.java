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
    private final EnrollmentRepository enrollmentRepository;

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

    /*
    * 동아리모임 수정폼
    * 동아리모임 수정
    * 동아리모임 삭제
    * */
    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable Long id, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        model.addAttribute("event",event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable Long id, @Valid EventForm eventForm, Errors errors,
                                    Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute("account",account);
            model.addAttribute("club",club);
            model.addAttribute("event",event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/club/" + club.getEncodedPath() +  "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/delete")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
        return "redirect:/club/" + club.getEncodedPath() + "/events";
    }


    /*
    * 모집인원참가자
    * 모집인원취소
    * */
    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getClubToEnroll(path);
        eventService.newEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/club/" + club.getEncodedPath() +  "/events/" + id;
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account,
                                   @PathVariable String path, @PathVariable Long id) {
        Club club = clubService.getClubToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/club/" + club.getEncodedPath() +  "/events/" + id;
    }

    /*
     * 참가신청확인
     * 참가신청취소
     * 체크인
     * 체크아웃
     * */
    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }


}
