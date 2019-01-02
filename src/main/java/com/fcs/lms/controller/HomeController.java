package com.fcs.lms.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fcs.lms.entity.Category;
import com.fcs.lms.entity.Course;
import com.fcs.lms.entity.Lecturer;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QuerySnapshot;

@Controller
public class HomeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	@GetMapping(value = "/")
	public String index(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> query = db.collection("courses").get();
		ApiFuture<QuerySnapshot> queryLec = null;
//		DocumentReference docRef;
//		ApiFuture<DocumentSnapshot> lecturer = null;

		List<Course> courses = query.get().toObjects(Course.class);
		List<Lecturer> lecturers = null;
		for (Course course : courses) {
//			docRef = db.collection("lecturers").document(course.getLecturerId());
//			lecturer = docRef.get();
			queryLec = db.collection("lecturers").whereEqualTo("name", course.getAuthor()).get();
			lecturers = queryLec.get().toObjects(Lecturer.class);
		}

		model.addAttribute("courses", courses);
		LOGGER.info(lecturers.toString());
		model.addAttribute("lecturers", lecturers);
		return "views/home/index";
	}

	@GetMapping(value = "/login")
	public String welcome() throws InterruptedException, ExecutionException {
		return "views/home/login";
	}

	@GetMapping(value = "/tos")
	public String tos() throws InterruptedException, ExecutionException {
		return "views/home/tos";
	}

	@GetMapping(value = "/privacy")
	public String privacy() throws InterruptedException, ExecutionException {
		return "views/home/privacy";
	}

	@GetMapping(value = "/list-courses/{category}")
	public String courseByCategory(Model model, @PathVariable("category") String name)
			throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		LOGGER.info("category not null");
		ApiFuture<QuerySnapshot> queryCategory = db.collection("categories").whereEqualTo("url", name).get();
		List<Category> categories = queryCategory.get().toObjects(Category.class);

		ApiFuture<QuerySnapshot> queryCourse = null;
		List<Course> courses = null;
		for (Category category : categories) {
			queryCourse = db.collection("courses").whereEqualTo("category", category.getName()).get();
			courses = queryCourse.get().toObjects(Course.class);
		}
		LOGGER.info("courses: " + courses.toString());
		model.addAttribute("courses", courses);
		return "views/home/courses";
	}

	@GetMapping(value = "/list-courses")
	public String courses(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> query = db.collection("courses").get();
		List<Course> courses = query.get().toObjects(Course.class);
		model.addAttribute("courses", courses);
		return "views/home/courses";
	}

	@GetMapping(value = "/detail")
	public String detail() throws InterruptedException, ExecutionException {
		return "views/home/detail";
	}

	@GetMapping(value = "/create")
	public String create() throws InterruptedException, ExecutionException {
		return "views/course/create";
	}
}
