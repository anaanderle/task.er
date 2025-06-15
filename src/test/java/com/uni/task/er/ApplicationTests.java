package com.uni.task.er;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.task.er.dto.request.*;
import com.uni.task.er.dto.response.*;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.model.*;
import com.uni.task.er.repository.*;
import com.uni.task.er.service.*;
import com.uni.task.er.mapper.*;
import com.uni.task.er.utils.PasswordUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ApplicationTests {

	@Test
	void contextLoads() {
	}

	// ==================== TESTES DE MODELO (ENTITY) ====================
	
	@Nested
	@DisplayName("Testes de Entidades/Modelos")
	class ModelTests {

		@Test
		@DisplayName("Deve criar um usuário válido")
		void shouldCreateValidUser() {
			// Given
			String name = "João Silva";
			LocalDate birthday = LocalDate.of(1990, 1, 1);
			String cellphone = "11999999999";
			String email = "joao@test.com";
			String password = "password123";

			// When
			User user = new User(name, birthday, cellphone, email, password);

			// Then
			assertThat(user.getName()).isEqualTo(name);
			assertThat(user.getBirthday()).isEqualTo(birthday);
			assertThat(user.getCellphone()).isEqualTo(cellphone);
			assertThat(user.getEmail()).isEqualTo(email);
			assertThat(user.getPassword()).isEqualTo(password);
			assertThat(user.getDeleted()).isFalse();
		}

		@Test
		@DisplayName("Deve criar uma tarefa válida")
		void shouldCreateValidTask() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			String title = "Tarefa Teste";
			String description = "Descrição da tarefa";
			String status = "PENDING";

			// When
			Task task = new Task(title, description, status, user);

			// Then
			assertThat(task.getTitle()).isEqualTo(title);
			assertThat(task.getDescription()).isEqualTo(description);
			assertThat(task.getStatus()).isEqualTo(status);
			assertThat(task.getUser()).isEqualTo(user);
		}

		@Test
		@DisplayName("Deve criar um webhook válido")
		void shouldCreateValidWebhook() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			String description = "Webhook Teste";
			String url = "https://example.com/webhook";

			// When
			Webhook webhook = new Webhook(description, url, user);

			// Then
			assertThat(webhook.getDescription()).isEqualTo(description);
			assertThat(webhook.getUrl()).isEqualTo(url);
			assertThat(webhook.getUser()).isEqualTo(user);
		}
	}

	// ==================== TESTES DE REPOSITÓRIO ====================
	
	@Nested
	@DataJpaTest
	@ActiveProfiles("test")
	@DisplayName("Testes de Repositório")
	class RepositoryTests {

		@Autowired
		private UserRepository userRepository;

		@Autowired
		private TaskRepository taskRepository;

		@Autowired
		private WebhookRepository webhookRepository;

		@Test
		@DisplayName("Deve salvar e encontrar usuário por email")
		void shouldSaveAndFindUserByEmail() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");

			// When
			User savedUser = userRepository.save(user);
			Optional<User> foundUser = userRepository.findByEmail("joao@test.com");

			// Then
			assertThat(savedUser.getId()).isNotNull();
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getEmail()).isEqualTo("joao@test.com");
		}

		@Test
		@DisplayName("Deve encontrar usuário por ID e não deletado")
		void shouldFindUserByIdAndNotDeleted() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			User savedUser = userRepository.save(user);

			// When
			Optional<User> foundUser = userRepository.findByIdAndDeletedFalse(savedUser.getId());

			// Then
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getDeleted()).isFalse();
		}

		@Test
		@DisplayName("Deve salvar tarefa e encontrar por usuário")
		void shouldSaveTaskAndFindByUser() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			User savedUser = userRepository.save(user);
			
			Task task = new Task("Tarefa Teste", "Descrição", "PENDING", savedUser);

			// When
			Task savedTask = taskRepository.save(task);
			List<Task> userTasks = taskRepository.findByUserId(savedUser.getId());

			// Then
			assertThat(savedTask.getId()).isNotNull();
			assertThat(userTasks).hasSize(1);
			assertThat(userTasks.get(0).getTitle()).isEqualTo("Tarefa Teste");
		}

		@Test
		@DisplayName("Deve salvar webhook e encontrar por usuário")
		void shouldSaveWebhookAndFindByUser() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			User savedUser = userRepository.save(user);
			
			Webhook webhook = new Webhook("Webhook Teste", "https://example.com", savedUser);

			// When
			Webhook savedWebhook = webhookRepository.save(webhook);
			List<Webhook> userWebhooks = webhookRepository.findByUserId(savedUser.getId());

			// Then
			assertThat(savedWebhook.getId()).isNotNull();
			assertThat(userWebhooks).hasSize(1);
			assertThat(userWebhooks.get(0).getDescription()).isEqualTo("Webhook Teste");
		}
	}

	// ==================== TESTES DE SERVIÇO ====================
	
	@Nested
	@ExtendWith(MockitoExtension.class)
	@DisplayName("Testes de Serviço")
	class ServiceTests {

		@Mock
		private UserRepository userRepository;

		@Mock
		private TaskRepository taskRepository;

		@Mock
		private WebhookRepository webhookRepository;

		@Mock
		private WebhookService webhookService;

		@Test
		@DisplayName("UserService - Deve criar usuário com sucesso")
		void userService_shouldCreateUserSuccessfully() {
			// Given
			UserService userService = new UserService(userRepository);
			UserCreateRequest request = new UserCreateRequest();
			request.setName("João");
			request.setBirthday(LocalDate.of(1990, 1, 1));
			request.setCellphone("11999999999");
			request.setEmail("joao@test.com");
			request.setPassword("password123");
			request.setConfirmPassword("password123");

			User savedUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "hashedPassword");
			savedUser.setId(1L);

			try (MockedStatic<PasswordUtils> passwordUtils = mockStatic(PasswordUtils.class)) {
				passwordUtils.when(() -> PasswordUtils.hashPassword("password123")).thenReturn("hashedPassword");
				when(userRepository.save(any(User.class))).thenReturn(savedUser);

				// When
				UserResponse response = userService.create(request);

				// Then
				assertThat(response.getId()).isEqualTo(1L);
				assertThat(response.getName()).isEqualTo("João");
				assertThat(response.getEmail()).isEqualTo("joao@test.com");
				verify(userRepository).save(any(User.class));
			}
		}

		@Test
		@DisplayName("UserService - Deve lançar exceção quando senhas não coincidem")
		void userService_shouldThrowExceptionWhenPasswordsDontMatch() {
			// Given
			UserService userService = new UserService(userRepository);
			UserCreateRequest request = new UserCreateRequest();
			request.setPassword("password123");
			request.setConfirmPassword("differentPassword");

			// When & Then
			assertThatThrownBy(() -> userService.create(request))
				.isInstanceOf(Exception.class);
		}

		@Test
		@DisplayName("TaskService - Deve criar tarefa com sucesso")
		void taskService_shouldCreateTaskSuccessfully() {
			// Given
			TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
			
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			user.setId(1L);
			
			TaskCreateRequest request = new TaskCreateRequest();
			request.setTitle("Nova Tarefa");
			request.setDescription("Descrição");
			request.setStatus("PENDING");
			request.setUserId(1L);

			Task savedTask = new Task("Nova Tarefa", "Descrição", "PENDING", user);
			savedTask.setId(1L);

			when(userRepository.findById(1L)).thenReturn(Optional.of(user));
			when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

			// When
			TaskResponse response = taskService.create(request);

			// Then
			assertThat(response.getId()).isEqualTo(1L);
			assertThat(response.getTitle()).isEqualTo("Nova Tarefa");
			assertThat(response.getUserId()).isEqualTo(1L);
			verify(taskRepository).save(any(Task.class));
			verify(webhookService).sendMessageByUser(eq(user), contains("criada"));
		}

		@Test
		@DisplayName("TaskService - Deve lançar exceção quando usuário não existe")
		void taskService_shouldThrowExceptionWhenUserNotFound() {
			// Given
			TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
			
			TaskCreateRequest request = new TaskCreateRequest();
			request.setUserId(999L);

			when(userRepository.findById(999L)).thenReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> taskService.create(request))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("User not found");
		}
	}

	// ==================== TESTES DE INTEGRAÇÃO (CONTROLLERS) ====================
	
	@Nested
	@WebMvcTest
	@DisplayName("Testes de Integração - Controllers")
	class ControllerIntegrationTests {

		@Autowired
		private MockMvc mockMvc;

		@Autowired
		private ObjectMapper objectMapper;

		@MockBean
		private UserService userService;

		@MockBean
		private TaskService taskService;

		@MockBean
		private WebhookService webhookService;

		@Test
		@DisplayName("UserController - Deve criar usuário via POST /users/register")
		void userController_shouldCreateUserViaPost() throws Exception {
			// Given
			UserCreateRequest request = new UserCreateRequest();
			request.setName("João");
			request.setEmail("joao@test.com");
			request.setPassword("password123");
			request.setConfirmPassword("password123");

			UserResponse response = new UserResponse(1L, "João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com");

			when(userService.create(any(UserCreateRequest.class))).thenReturn(response);

			// When & Then
			mockMvc.perform(post("/users/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("João"))
				.andExpect(jsonPath("$.email").value("joao@test.com"));
		}

		@Test
		@DisplayName("TaskController - Deve criar tarefa via POST /tasks")
		void taskController_shouldCreateTaskViaPost() throws Exception {
			// Given
			TaskCreateRequest request = new TaskCreateRequest();
			request.setTitle("Nova Tarefa");
			request.setDescription("Descrição");
			request.setStatus("PENDING");
			request.setUserId(1L);

			TaskResponse response = new TaskResponse();
			response.setId(1L);
			response.setTitle("Nova Tarefa");
			response.setDescription("Descrição");
			response.setStatus("PENDING");
			response.setUserId(1L);

			when(taskService.create(any(TaskCreateRequest.class))).thenReturn(response);

			// When & Then
			mockMvc.perform(post("/tasks")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Nova Tarefa"))
				.andExpect(jsonPath("$.status").value("PENDING"));
		}

		@Test
		@DisplayName("UserController - Deve buscar usuário por ID via GET /users/{id}")
		void userController_shouldGetUserById() throws Exception {
			// Given
			UserResponse response = new UserResponse(1L, "João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com");

			when(userService.getById(1L)).thenReturn(response);

			// When & Then
			mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("João"));
		}
	}

	// ==================== TESTES DE MAPPER ====================
	
	@Nested
	@DisplayName("Testes de Mapper")
	class MapperTests {

		@Test
		@DisplayName("UserMapper - Deve converter UserCreateRequest para User")
		void userMapper_shouldConvertCreateRequestToUser() {
			// Given
			UserCreateRequest request = new UserCreateRequest();
			request.setName("João");
			request.setBirthday(LocalDate.of(1990, 1, 1));
			request.setCellphone("11999999999");
			request.setEmail("joao@test.com");
			request.setPassword("password123");

			// When
			User user = UserMapper.toModel(request);

			// Then
			assertThat(user.getName()).isEqualTo("João");
			assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
			assertThat(user.getCellphone()).isEqualTo("11999999999");
			assertThat(user.getEmail()).isEqualTo("joao@test.com");
			assertThat(user.getPassword()).isEqualTo("password123");
		}

		@Test
		@DisplayName("TaskMapper - Deve converter TaskCreateRequest para Task")
		void taskMapper_shouldConvertCreateRequestToTask() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			TaskCreateRequest request = new TaskCreateRequest();
			request.setTitle("Tarefa Teste");
			request.setDescription("Descrição");
			request.setStatus("PENDING");

			// When
			Task task = TaskMapper.toModel(request, user);

			// Then
			assertThat(task.getTitle()).isEqualTo("Tarefa Teste");
			assertThat(task.getDescription()).isEqualTo("Descrição");
			assertThat(task.getStatus()).isEqualTo("PENDING");
			assertThat(task.getUser()).isEqualTo(user);
		}

		@Test
		@DisplayName("TaskMapper - Deve converter Task para TaskResponse")
		void taskMapper_shouldConvertTaskToResponse() {
			// Given
			User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
			user.setId(1L);
			Task task = new Task("Tarefa Teste", "Descrição", "PENDING", user);
			task.setId(1L);

			// When
			TaskResponse response = TaskMapper.toResponse(task);

			// Then
			assertThat(response.getId()).isEqualTo(1L);
			assertThat(response.getTitle()).isEqualTo("Tarefa Teste");
			assertThat(response.getDescription()).isEqualTo("Descrição");
			assertThat(response.getStatus()).isEqualTo("PENDING");
			assertThat(response.getUserId()).isEqualTo(1L);
			assertThat(response.getUserName()).isEqualTo("João");
		}
	}

	// ==================== TESTES DE UTILITÁRIOS ====================
	
	@Nested
	@DisplayName("Testes de Utilitários")
	class UtilityTests {

		@Test
		@DisplayName("PasswordUtils - Deve fazer hash e verificar senha")
		void passwordUtils_shouldHashAndCheckPassword() {
			// Given
			String plainPassword = "password123";

			// When
			String hashedPassword = PasswordUtils.hashPassword(plainPassword);
			boolean isValid = PasswordUtils.checkPassword(plainPassword, hashedPassword);
			boolean isInvalid = PasswordUtils.checkPassword("wrongPassword", hashedPassword);

			// Then
			assertThat(hashedPassword).isNotEqualTo(plainPassword);
			assertThat(isValid).isTrue();
			assertThat(isInvalid).isFalse();
		}
	}
}
