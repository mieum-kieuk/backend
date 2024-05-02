package archivegarden.shop.controller;

import archivegarden.shop.dto.community.qna.AddQnaForm;
import archivegarden.shop.dto.community.qna.EditQnaForm;
import archivegarden.shop.dto.community.qna.QnaDetailsDto;
import archivegarden.shop.dto.community.qna.QnaListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.service.community.QnaService;
import archivegarden.shop.web.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/community/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @GetMapping
    public String qnas(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, 5);
        Page<QnaListDto> qnaDtos = qnaService.getQnas(pageRequest);
        model.addAttribute("qnas", qnaDtos);
        return "community/qna/qna_list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String addQnAForm(@ModelAttribute("qna") AddQnaForm form) {
        return "community/qna/add_qna";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String addQnA(@Valid @ModelAttribute("qna") AddQnaForm form, BindingResult bindingResult,
                         @CurrentUser Member loginMember, RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            return "community/qna/add_qna";
        }

        Long qnaId = qnaService.saveQnA(form, loginMember);
        redirectAttributes.addAttribute("qnaId", qnaId);
        return "redirect:/community/qna/{qnaId}";
    }

    @GetMapping("/{qnaId}")
    public String qna(@PathVariable("qnaId") Long qnaId, Model model) {
        QnaDetailsDto qnaDetailsDto = qnaService.getQna(qnaId);
        model.addAttribute("qna", qnaDetailsDto);
        return "community/qna/qna_details";
    }

    @GetMapping("/{qnaId}/edit")
    public String editQnaForm(@PathVariable("qnaId") Long qnaId, Model model) {
        EditQnaForm form = qnaService.getEditQnaForm(qnaId);
        model.addAttribute("qna", form);
        return "community/qna/edit_qna";
    }

    @GetMapping("/{qnaId}/delete")
    public String deleteQna(@PathVariable("qnaId") Long qnaId) {
        qnaService.deleteQna(qnaId);
        return "redirect:/community/qna";
    }
}
