# NutriTrack - Comprehensive Nutrition Tracking App

[![Android](https://img.shields.io/badge/Android-API%2031+-green.svg)](https://android-arsenal.com/api?level=31)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-orange.svg)](https://developer.android.com/jetpack/compose)

NutriTrack is a sophisticated Android nutrition tracking application built with modern Android development tools and libraries. The app provides personalized nutrition coaching through AI, comprehensive user insights, and specialized tools for healthcare professionals.

## 🌟 Features

- **User Authentication & Registration**: Secure user management with BCrypt password hashing
- **Personalized Questionnaires**: Multi-step onboarding to capture user preferences and personas
- **AI-Powered Nutrition Coach**: Real-time chat with Gemini AI for personalized nutrition advice
- **Score Tracking & Insights**: Comprehensive nutrition scoring with historical data visualization
- **Clinician Dashboard**: Professional tools for healthcare providers to monitor patients
- **Dark/Light Theme Support**: Modern Material Design 3 theming
- **Offline-First Architecture**: Room database for local data persistence
- **Real-time Data Sync**: Seamless data synchronization across devices

## 🏗️ Architecture

The application follows Clean Architecture principles with:

```
├── Presentation Layer (View)
│   ├── Composable UI Components
│   ├── Screen Composables
│   └── Navigation Setup
├── Business Logic Layer (ViewModel)
│   ├── State Management
│   ├── Business Rules
│   └── User Interactions
└── Data Layer (Model)
    ├── Room Database
    ├── Repository Pattern
    ├── Network APIs
    └── Data Sources
```

## 📚 Documentation

This project includes comprehensive documentation covering all aspects of the application:

### 🚀 [Complete API Documentation](./API_DOCUMENTATION.md)
The main documentation file covering:
- Application overview and architecture
- Getting started guide
- Navigation system
- Data layer (entities, DAOs, repositories)
- ViewModels and state management
- UI components and screens
- Utilities and helper classes
- API integration
- Theme and styling
- Usage examples with code samples

### 🗄️ [Database Documentation](./DATABASE_DOCUMENTATION.md)
Detailed database documentation including:
- Database schema and entity relationships
- Room configuration and migrations
- DAO interfaces with all available methods
- Repository pattern implementation
- Type converters and database initialization
- Performance optimization strategies
- Usage examples for database operations

### 🎨 [UI Components Documentation](./UI_COMPONENTS_DOCUMENTATION.md)
Comprehensive UI components guide covering:
- Core reusable components (ScoreProgressBar, TimePickerComponent, etc.)
- Navigation components (BottomNavigationBar, TopAppBar)
- Authentication components (AuthenticationButton, UserIdDropdown)
- Form components with validation
- Chart components for data visualization
- Animation components and page transitions
- Best practices for component composition
- Accessibility and performance guidelines

### 🏛️ [ViewModels Documentation](./VIEWMODEL_DOCUMENTATION.md)
Complete ViewModels and state management guide including:
- MVVM architecture implementation
- State management patterns with UiState
- Dependency injection with ViewModelProviderFactory
- Detailed documentation for all ViewModels:
  - AuthViewModel (authentication and registration)
  - GenAIViewModel (AI chat functionality)
  - QuestionnaireViewModel (multi-step questionnaire)
  - ClinicianDashboardViewModel (healthcare professional tools)
  - UserStatsViewModel (statistics and scoring)
- Testing strategies for ViewModels
- Performance optimization techniques

## 🛠️ Technology Stack

### Core Technologies
- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with SQLite
- **Navigation**: Navigation Compose
- **Async Programming**: Coroutines and Flow

### Key Libraries
- **AI Integration**: Google Generative AI (Gemini)
- **Image Loading**: Coil for Compose
- **Networking**: Retrofit with Gson converter
- **Security**: BCrypt for password hashing
- **Splash Screen**: AndroidX Core SplashScreen
- **Dependency Injection**: Manual DI with Factory pattern

### Development Tools
- **Build System**: Gradle with Kotlin DSL
- **Code Generation**: KSP (Kotlin Symbol Processing)
- **Database Schema**: Room with auto-generated DAOs

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK API 31 or higher
- Kotlin 1.9.0 or newer

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/nutritrack.git
   cd nutritrack
   ```

2. **Set up API keys**
   Create a `secrets.properties` file in the root directory:
   ```properties
   API_KEY=your_gemini_api_key_here
   ```

3. **Build and run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### Configuration

The app requires minimal configuration:
- **API Keys**: Add your Gemini AI API key to `secrets.properties`
- **Database**: Automatically initialized on first run with sample data
- **Permissions**: Internet permission for AI functionality

## 📱 Usage

### For End Users
1. **Registration**: Create an account or use existing user data
2. **Questionnaire**: Complete the onboarding questionnaire to set preferences
3. **Home Dashboard**: View nutrition scores and personalized insights
4. **AI Coach**: Chat with the nutrition coach for personalized advice
5. **Insights**: Track progress over time with detailed analytics

### For Healthcare Professionals
1. **Clinician Dashboard**: Access specialized dashboard for patient overview
2. **Patient Management**: View and monitor multiple patients
3. **AI Recommendations**: Generate professional nutrition recommendations
4. **Data Export**: Export patient data for further analysis

## 🧪 Testing

The project includes comprehensive testing strategies:

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### UI Tests
```bash
./gradlew connectedCheck
```

## 📈 Performance

The app is optimized for performance with:
- **Lazy Loading**: Efficient list rendering with LazyColumn/LazyRow
- **Database Indexing**: Optimized queries with proper indexes
- **Memory Management**: Proper lifecycle-aware components
- **Image Optimization**: Efficient image loading with Coil
- **Animation Performance**: Hardware-accelerated animations

## 🔒 Security

Security measures implemented:
- **Password Hashing**: BCrypt for secure password storage
- **API Key Management**: Secure API key handling with build variants
- **Data Validation**: Input validation at all layers
- **SQL Injection Prevention**: Parameterized queries with Room

## 🌐 Accessibility

The app follows Android accessibility guidelines:
- **Content Descriptions**: Proper content descriptions for screen readers
- **Color Contrast**: WCAG-compliant color contrast ratios
- **Touch Targets**: Minimum 48dp touch target sizes
- **Keyboard Navigation**: Full keyboard navigation support

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add documentation for public APIs
- Include unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Developer**: Xingyu Yang
- **Student ID**: 33533563
- **Course**: FIT2081 Assignment 1

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation files for detailed information
- Review the code examples in the documentation

## 🔄 Version History

### Version 1.0.0 (Current)
- Initial release with core functionality
- User authentication and registration
- AI-powered nutrition coaching
- Comprehensive questionnaire system
- Clinician dashboard
- Score tracking and insights

## 🚀 Future Enhancements

Planned features for future releases:
- Push notifications for meal reminders
- Integration with fitness trackers
- Social features for community support
- Advanced analytics and reporting
- Multi-language support
- Offline AI recommendations

---

## Quick Navigation

- **[📖 Complete API Documentation](./API_DOCUMENTATION.md)** - Start here for a comprehensive overview
- **[🗄️ Database Documentation](./DATABASE_DOCUMENTATION.md)** - Database schema and operations
- **[🎨 UI Components Documentation](./UI_COMPONENTS_DOCUMENTATION.md)** - UI components and design system
- **[🏛️ ViewModels Documentation](./VIEWMODEL_DOCUMENTATION.md)** - State management and business logic

---

*Built with ❤️ using modern Android development practices*