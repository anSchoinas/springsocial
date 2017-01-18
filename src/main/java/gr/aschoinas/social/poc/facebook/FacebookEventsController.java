package gr.aschoinas.social.poc.facebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by Andreas Schoinas
 */
@Controller
public class FacebookEventsController {

	private final Facebook facebook;

	private static final Logger LOG = LoggerFactory.getLogger(FacebookEventsController.class);

	@Inject
	public FacebookEventsController(Facebook facebook) {
		this.facebook = facebook;
	}

	@RequestMapping(value="/facebook/events", method= RequestMethod.GET)
	public String showFeed(Model model) {
		PagedList<Post> feed = facebook.feedOperations().getFeed();
		ArrayList<Post> events = new ArrayList<Post>();

		for(Post post : feed){
			if(post.getPlace() != null){
				events.add(post);
				LOG.info("Post: " + post);

			}
		}

		model.addAttribute("feed", events);
		return "facebook/events";
	}

	@RequestMapping(value="/facebook/events", method=RequestMethod.POST)
	public String postUpdate(String message) {
		facebook.feedOperations().updateStatus(message);
		return "redirect:/facebook/events";
	}

}
