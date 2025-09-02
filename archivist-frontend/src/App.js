import React, { useState } from 'react';
import Navigation from './components/Navigation';
import { useAuth } from './context/AuthContext';
import LoginForm from './components/LoginForm';
import RegistrationForm from './components/RegistrationForm';
import NoteForm from './components/NoteForm';
import NoteList from './components/NoteList';
import CaseForm from './components/CaseForm'; // <-- Импортируем новый компонент
import './App.css';

function App() {
  const { currentUser, logout } = useAuth();
  const [view, setView] = useState('dashboard');

  if (!currentUser) {
    // Формы входа и регистрации можно оставить как есть или тоже обернуть
    // в .form-container для консистентности
    return (
        <div className="App">
            <header className="App-header">
                <h1>Архив Детективного Агентства</h1>
                <div className="form-container">
                    <LoginForm />
                </div>
                <hr />
                <div className="form-container">
                    <RegistrationForm />
                </div>
            </header>
        </div>
    );
  }

  // Функция для рендера контента в зависимости от view
  const renderContent = () => {
    switch (view) {
      case 'addCase':
        return <div className="form-container"><CaseForm onCaseAdded={() => setView('dashboard')} /></div>;
      case 'addNote':
        return <div className="form-container"><NoteForm onNoteAdded={() => setView('viewNotes')} /></div>;
      case 'viewNotes':
        return <NoteList />;
      default:
        // Можно добавить приветственное сообщение на dashboard
        return <h2 style={{color: '#a0aec0'}}>Выберите действие</h2>;
    }
  };


  return (
    <div className="App">
      <header className="App-header">
        <h1>Архив Детективного Агентства</h1>
        <p>
            Добро пожаловать, агент {currentUser.username}!
            {/* Добавляем классы кнопке выхода */}
            <button onClick={logout} className="btn btn-secondary" style={{marginLeft: '15px'}}>Выйти</button>
        </p>
        <hr />

        <Navigation view={view} setView={setView} />

        {/* Рендерим основной контент */}
        <div className="main-content">
            {renderContent()}
        </div>

      </header>
    </div>
  );
}

export default App;