package com.yeonsung.modules.club;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonsung.modules.account.Account;
import com.yeonsung.modules.account.CurrentAccount;
import com.yeonsung.modules.club.form.ClubDescriptionForm;
import com.yeonsung.modules.tag.Tag;
import com.yeonsung.modules.tag.TagForm;
import com.yeonsung.modules.tag.TagRepository;
import com.yeonsung.modules.tag.TagService;
import com.yeonsung.modules.zone.Zone;
import com.yeonsung.modules.zone.ZoneForm;
import com.yeonsung.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/club/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;


    /*
    * 동아리 글 수정
    * */
    @GetMapping("/description")
    public String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));
        return "club/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid ClubDescriptionForm clubDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute("account",account);
            model.addAttribute("club",club);
            return "club/settings/description";
        }

        clubService.updateClubDescription(club,clubDescriptionForm);
        attributes.addFlashAttribute("message", "동아리 소개를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/description";
    }


    /*
    * 배너 수정
    * 배너 활성화
    * 배너 비활성화
    * */
    @GetMapping("/banner")
    public String clubImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        return "club/settings/banner";
    }

    @PostMapping("/banner")
    public String clubImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.updateClubImage(club, image);
        attributes.addFlashAttribute("message", "동아리 이미지를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableClubBanner(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.enableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableClubBanner(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.disableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }


    /*
    * 태그뷰
    * 태그추가
    * 태그제거
    * */
    @GetMapping("/tags")
    public String clubTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);

        model.addAttribute("tags", club.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "club/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path,
                                 @RequestBody TagForm tagForm) {
        Club club = clubService.getClubToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        clubService.addTag(club, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm) {
        Club club = clubService.getClubToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeTag(club, tag);
        return ResponseEntity.ok().build();
    }


    /*
    * 지역뷰
    * 지역추가
    * 지역제거
    * */
    @GetMapping("/zones")
    public String clubZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {

        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        model.addAttribute("zones", club.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "club/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Club club = clubService.getClubToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.addZone(club, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {
        Club club = clubService.getClubToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeZone(club, zone);
        return ResponseEntity.ok().build();
    }

    /*
    * 동아리 설정폼
    * 동아리 공개
    * 동아리 종료
    * 동아리 인원모집시작
    * 동아리 인원모집종료
    * */
    @GetMapping("/club")
    public String clubSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/club";
    }

    @PostMapping("/club/publish")
    public String publishClub(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        clubService.publish(club);
        attributes.addFlashAttribute("message", "동아리를 공개했습니다!");
        return "redirect:/club/" +club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/close")
    public String closeClub(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        clubService.close(club);
        attributes.addFlashAttribute("message", "돟아리를 종료했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }

        clubService.startRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path,
                              RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }

        clubService.stopRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    /*
    * 동아리 경로 수정
    * 동아리 제목 수정
    * 동아리 삭제
    * */

    @PostMapping("/club/path")
    public String updateClubPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                  Model model, RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        if (!clubService.isValidPath(newPath)) {
            model.addAttribute("account",account);
            model.addAttribute("club",club);
            model.addAttribute("clubPathError", "해당 동아리 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "club/settings/club";
        }

        clubService.updateClubPath(club, newPath);
        attributes.addFlashAttribute("message", "동아리 경로를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/title")
    public String updateStudyTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        if (!clubService.isValidTitle(newTitle)) {
            model.addAttribute("account",account);
            model.addAttribute("club",club);
            model.addAttribute("clubTitleError", "동아리 이름을 다시 입력하세요.");
            return "club/settings/club";
        }

        clubService.updateClubTitle(club, newTitle);
        attributes.addFlashAttribute("message", "동아리 이름을 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/remove")
    public String removeStudy(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getStudyToUpdateStatus(account, path);
        clubService.remove(club);
        return "redirect:/";
    }

}
