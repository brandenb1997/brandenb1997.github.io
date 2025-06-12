///////////////////////////////////////////////////////////////////////////////
// viewmanager.cpp
// ============
// manage the viewing of 3D objects within the viewport - camera, projection
//
//  AUTHOR: Brian Battersby - SNHU Instructor / Computer Science
//	Created for CS-330-Computational Graphics and Visualization, Nov. 1st, 2023
///////////////////////////////////////////////////////////////////////////////

#include "ViewManager.h"

// GLM Math Header inclusions
#include <glm/glm.hpp>
#include <glm/gtx/transform.hpp>
#include <glm/gtc/type_ptr.hpp>    

// declaration of the global variables and defines
namespace
{
	const int WINDOW_WIDTH = 1000;
	const int WINDOW_HEIGHT = 800;
	const char* g_ViewName = "view";
	const char* g_ProjectionName = "projection";

	Camera* g_pCamera = nullptr;

	float gLastX = WINDOW_WIDTH / 2.0f;
	float gLastY = WINDOW_HEIGHT / 2.0f;
	bool gFirstMouse = true;

	float gDeltaTime = 0.0f;
	float gLastFrame = 0.0f;

	bool bOrthographicProjection = false;
}

// Object focus targets and camera positions
const int NUM_OBJECTS = 5;

glm::vec3 objectPositions[NUM_OBJECTS] = {
	glm::vec3(-5.0f, 1.0f, -1.5f),  // Bottle
	glm::vec3(2.0f, 1.0f, -4.5f),   // Blue Book
	glm::vec3(-1.5f, 1.0f, 4.0f),   // Pink Book
	glm::vec3(5.5f, 1.0f, 1.0f),    // Post-it Note
	glm::vec3(0.0f, 10.0f, -10.0f)  // Wall Plane
};

glm::vec3 cameraFocusPoints[NUM_OBJECTS] = {
	glm::vec3(-5.0f, 3.0f, 5.0f),
	glm::vec3(2.0f, 3.0f, 5.0f),
	glm::vec3(-1.5f, 3.0f, 10.0f),
	glm::vec3(5.5f, 3.0f, 8.0f),
	glm::vec3(0.0f, 15.0f, -2.0f)
};

/***********************************************************
 *  ViewManager()
 ***********************************************************/
ViewManager::ViewManager(ShaderManager* pShaderManager)
{
	m_pShaderManager = pShaderManager;
	m_pWindow = NULL;
	g_pCamera = new Camera();
	g_pCamera->Position = glm::vec3(0.0f, 5.0f, 12.0f);
	g_pCamera->Front = glm::vec3(0.0f, -0.5f, -2.0f);
	g_pCamera->Up = glm::vec3(0.0f, 1.0f, 0.0f);
	g_pCamera->Zoom = 80;
	g_pCamera->MovementSpeed = 20;
}

/***********************************************************
 *  ~ViewManager()
 ***********************************************************/
ViewManager::~ViewManager()
{
	m_pShaderManager = NULL;
	m_pWindow = NULL;
	if (g_pCamera) delete g_pCamera;
	g_pCamera = NULL;
	bOrthographicProjection = false;
}

/***********************************************************
 *  CreateDisplayWindow()
 ***********************************************************/
GLFWwindow* ViewManager::CreateDisplayWindow(const char* windowTitle)
{
	GLFWwindow* window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, windowTitle, NULL, NULL);
	if (!window)
	{
		std::cout << "Failed to create GLFW window" << std::endl;
		glfwTerminate();
		return NULL;
	}
	glfwMakeContextCurrent(window);
	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	glfwSetCursorPosCallback(window, &ViewManager::Mouse_Position_Callback);
	glfwSetScrollCallback(window, &ViewManager::Scroll_Callback);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	m_pWindow = window;
	return window;
}

/***********************************************************
 *  Mouse_Position_Callback()
 ***********************************************************/
void ViewManager::Mouse_Position_Callback(GLFWwindow* window, double xMousePos, double yMousePos)
{
	if (gFirstMouse)
	{
		gLastX = xMousePos;
		gLastY = yMousePos;
		gFirstMouse = false;
	}

	float xOffset = xMousePos - gLastX;
	float yOffset = gLastY - yMousePos;

	gLastX = xMousePos;
	gLastY = yMousePos;

	g_pCamera->ProcessMouseMovement(xOffset, yOffset);
}

/***********************************************************
 *  Scroll_Callback()
 ***********************************************************/
void ViewManager::Scroll_Callback(GLFWwindow* window, double xoffset, double yoffset)
{
	bool isMoving =
		glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS ||
		glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS ||
		glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS ||
		glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS;

	if (!isMoving)
	{
		g_pCamera->ProcessMouseScroll((float)yoffset);
	}
	else
	{
		g_pCamera->MovementSpeed += yoffset * 2.0f;
		if (g_pCamera->MovementSpeed < 2.0f)
			g_pCamera->MovementSpeed = 2.0f;
		else if (g_pCamera->MovementSpeed > 50.0f)
			g_pCamera->MovementSpeed = 50.0f;

	}
}

/***********************************************************
 *  ProcessKeyboardEvents()
 ***********************************************************/
void ViewManager::ProcessKeyboardEvents()
{
	if (glfwGetKey(m_pWindow, GLFW_KEY_ESCAPE) == GLFW_PRESS)
		glfwSetWindowShouldClose(m_pWindow, true);

	if (glfwGetKey(m_pWindow, GLFW_KEY_W) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(FORWARD, gDeltaTime);
	if (glfwGetKey(m_pWindow, GLFW_KEY_S) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(BACKWARD, gDeltaTime);
	if (glfwGetKey(m_pWindow, GLFW_KEY_A) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(LEFT, gDeltaTime);
	if (glfwGetKey(m_pWindow, GLFW_KEY_D) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(RIGHT, gDeltaTime);
	if (glfwGetKey(m_pWindow, GLFW_KEY_Q) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(UP, gDeltaTime);
	if (glfwGetKey(m_pWindow, GLFW_KEY_E) == GLFW_PRESS)
		g_pCamera->ProcessKeyboard(DOWN, gDeltaTime);


	/////////////////////////////////////////////////////
	////// Object Focused Perspectives (keys 1–5)///////
	///////////////////////////////////////////////////

	if (glfwGetKey(m_pWindow, GLFW_KEY_1) == GLFW_PRESS) {
		g_pCamera->Position = cameraFocusPoints[0];
		g_pCamera->Front = glm::normalize(objectPositions[0] - g_pCamera->Position);
	}
	if (glfwGetKey(m_pWindow, GLFW_KEY_2) == GLFW_PRESS) {
		g_pCamera->Position = cameraFocusPoints[1];
		g_pCamera->Front = glm::normalize(objectPositions[1] - g_pCamera->Position);
	}
	if (glfwGetKey(m_pWindow, GLFW_KEY_3) == GLFW_PRESS) {
		g_pCamera->Position = cameraFocusPoints[2];
		g_pCamera->Front = glm::normalize(objectPositions[2] - g_pCamera->Position);
	}
	if (glfwGetKey(m_pWindow, GLFW_KEY_4) == GLFW_PRESS) {
		g_pCamera->Position = cameraFocusPoints[3];
		g_pCamera->Front = glm::normalize(objectPositions[3] - g_pCamera->Position);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////
	// Axis Focus Points (X, Y, Z keys)////
	//////////////////////////////////////

	if (glfwGetKey(m_pWindow, GLFW_KEY_X) == GLFW_PRESS) {
		// View from the +X axis looking toward origin
		g_pCamera->Position = glm::vec3(20.0f, 0.0f, 0.0f);
		g_pCamera->Front = glm::normalize(glm::vec3(0.0f, 0.0f, 0.0f) - g_pCamera->Position);
		g_pCamera->Up = glm::vec3(0.0f, 1.0f, 0.0f);
	}

	if (glfwGetKey(m_pWindow, GLFW_KEY_Y) == GLFW_PRESS) {
		// View from the +Y axis (top-down)
		g_pCamera->Position = glm::vec3(0.0f, 20.0f, 0.0f);
		g_pCamera->Front = glm::normalize(glm::vec3(0.0f, 0.0f, 0.0f) - g_pCamera->Position);
		g_pCamera->Up = glm::vec3(0.0f, 0.0f, -1.0f); // Flip up vector for top-down view
	}

	if (glfwGetKey(m_pWindow, GLFW_KEY_Z) == GLFW_PRESS) {
		// View from the +Z axis looking toward origin
		g_pCamera->Position = glm::vec3(0.0f, 0.0f, 20.0f);
		g_pCamera->Front = glm::normalize(glm::vec3(0.0f, 0.0f, 0.0f) - g_pCamera->Position);
		g_pCamera->Up = glm::vec3(0.0f, 1.0f, 0.0f);
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////


	// Toggle projection view
	if (glfwGetKey(m_pWindow, GLFW_KEY_P) == GLFW_PRESS)
		bOrthographicProjection = false;
	if (glfwGetKey(m_pWindow, GLFW_KEY_O) == GLFW_PRESS)
		bOrthographicProjection = true;
}

/***********************************************************
 *  PrepareSceneView()
 ***********************************************************/
void ViewManager::PrepareSceneView()
{
	glm::mat4 view;
	glm::mat4 projection;

	float currentFrame = glfwGetTime();
	gDeltaTime = currentFrame - gLastFrame;
	gLastFrame = currentFrame;

	ProcessKeyboardEvents();

	view = g_pCamera->GetViewMatrix();

	if (bOrthographicProjection)
	{
		float orthoSize = 15.0f;
		float aspectRatio = (float)WINDOW_WIDTH / (float)WINDOW_HEIGHT;
		projection = glm::ortho(-orthoSize * aspectRatio, orthoSize * aspectRatio, -orthoSize, orthoSize, 0.1f, 100.0f);
	}
	else
	{
		projection = glm::perspective(glm::radians(g_pCamera->Zoom), (GLfloat)WINDOW_WIDTH / (GLfloat)WINDOW_HEIGHT, 0.1f, 100.0f);
	}

	if (m_pShaderManager)
	{
		m_pShaderManager->setMat4Value(g_ViewName, view);
		m_pShaderManager->setMat4Value(g_ProjectionName, projection);
		m_pShaderManager->setVec3Value("viewPosition", g_pCamera->Position);
	}
}
