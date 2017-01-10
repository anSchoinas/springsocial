package gr.aschoinas.social.poc.facebook;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

/**
 * Created by Andreas Schoinas
 */
@Controller
public class FacebookEventsController {

	private final Facebook facebook;

	@Inject
	public FacebookEventsController(Facebook facebook) {
		this.facebook = facebook;
	}

	@RequestMapping(value="/facebook/events", method= RequestMethod.GET)
	public String showFeed(Model model) {
		model.addAttribute("feed", facebook.feedOperations().getCheckins());
		return "facebook/events";
	}

	@RequestMapping(value="/facebook/events", method=RequestMethod.POST)
	public String postUpdate(String message) {
		facebook.feedOperations().updateStatus(message);
		return "redirect:/facebook/events";
	}

}
