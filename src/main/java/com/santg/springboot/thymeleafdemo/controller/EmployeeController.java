package com.santg.springboot.thymeleafdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.santg.springboot.thymeleafdemo.entity.Employee;
import com.santg.springboot.thymeleafdemo.service.EmployeeService;

@SpringBootApplication (exclude = {ContextStackAutoConfiguration.class, ContextRegionProviderAutoConfiguration.class})
@Controller
@RequestMapping("/employees")
public class EmployeeController {

	private EmployeeService employeeService;
	
	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	 @Autowired
	    private AmazonSNSClient snsClient;

	    String TOPIC_ARN= "arn:aws:sns:us-east-1:705258768232:springboot-crud-mysql" ;

		@GetMapping("/addSubscription/{email}")
		public String addSubscription(@PathVariable String email, RedirectAttributes redirectAttributes) {
			SubscribeRequest request = new SubscribeRequest(TOPIC_ARN, "email", email);
			snsClient.subscribe(request);
			redirectAttributes.addFlashAttribute("message", "You have Successfully subscribed. To confirm the subscription, please check your email : "+email);
			return "redirect:/employees/list";
		}

		 @GetMapping("/sendNotification")
		public String publishMessageToTopic(RedirectAttributes redirectAttributes){
			 PublishRequest publishRequest=new PublishRequest(TOPIC_ARN,buildEmailBody(),"Notification: Its holiday Time");
			 snsClient.publish(publishRequest);
			 redirectAttributes.addFlashAttribute("notification", "Users have received a notification. Please check your emails");
			 return "redirect:/employees/list";
		}



		private String buildEmailBody(){
			return "Hello Friend,\n" +
					"\n" +
					"Its holiday Time"+"\n"+
					"Happy Thanksgiving and christmas \n" +
					"\n" +
					"\n" +
					"Please check the following link for thanksgiving and blackfriday deals:" +
					"\n" +
					"https://www.businessinsider.com/black-friday-deals";
		}
	
	// add mapping for "/list"
	@GetMapping("/list")
	public String employeeList(Model model) {
	
		// get employees from data base
		List<Employee> employees = employeeService.findAll();
		
		// add to the spring model
		model.addAttribute("employees", employees);
	 
		return "employees/list-employees";
	}
	
	// add mapping for "/add" to add new employees
	@GetMapping("/add")
	public String addEmployee(Model model) {
		
		// create model attribute to bind form data
		Employee employee = new Employee();
		
		model.addAttribute("employee", employee);
		
		return "employees/employee-form";
	}
	
	@GetMapping("/update")
	public String updateEmployee(@RequestParam("employeeId") int id, Model model) {
		
		// get the employee from the service
		Employee employee = employeeService.findById(id);
		
		// set employee as a model attribute to pre-populate the form
		model.addAttribute("employee", employee);
		
		// send over to our form
		return "employees/employee-form";
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("employeeId") int id) {
		
		// delete employee
		employeeService.deleteById(id);
		
		// return to list
		return "redirect:/employees/list";
	}
	
	@PostMapping("/save")
	public String saveEmployee(@ModelAttribute("employee") Employee employee) {
		
		// save the employee
		employeeService.save(employee);
		
		// use a redirect to prevent duplicated submissions
		return "redirect:/employees/list";
	}
}
