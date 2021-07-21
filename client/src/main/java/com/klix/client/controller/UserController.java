package com.klix.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.klix.client.model.BankOffers;
import com.klix.client.model.LoanRequest;
import com.klix.client.service.ErrorWrapper;
import com.klix.client.service.LoanRequestService;

@Controller
public class UserController {

	private static final String REVIEW_PAGE = "review";
	private static final String REQUEST_PAGE = "request";
	private static final String REDIRECT_PREFIX = "redirect:/";

	@Autowired
	private LoanRequestService service;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("lreq", new LoanRequest());
		return REQUEST_PAGE;
	}

	@GetMapping("/{id}")
	public String status(@PathVariable("id") String id, Model model) {
		model.addAttribute("data", service.getOffers(id));
		return REVIEW_PAGE;
	}

	@PostMapping
	public String create(LoanRequest lreq, Model model, RedirectAttributes ra) {

		ErrorWrapper<BankOffers> wrapper = service.createRequest(lreq);

		if (wrapper.getErrors() != null) {
			model.addAttribute("lreq", lreq);
			model.addAttribute("err", wrapper.getErrors());
			return REQUEST_PAGE;
		}

		ra.addFlashAttribute("data", wrapper.getResponse());
		return REDIRECT_PREFIX + wrapper.getResponse().getId();
	}

}
