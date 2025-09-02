import React, { useState } from 'react';
import { useAuth } from './context/AuthContext';
import LoginForm from './components/LoginForm';
import RegistrationForm from './components/RegistrationForm';
import NoteForm from './components/NoteForm';
import NoteList from './components/NoteList';
import CaseForm from './components/CaseForm'; // <-- Импортируем новый компонент
import './App.css';

function App() {
  const { currentUser, logout } = useAuth();
  const [view, setView] = useState('dashboard'); // 'dashboard', 'addCase', 'addNote', 'viewNotes'

  if (!currentUser) {
    return (
        <div className="App">
            <header className="App-header">
                <h1>Архив Детективного Агентства</h1>
                <LoginForm />
                <hr />
                <RegistrationForm />
            </header>
        </div>
    );
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Архив Детективного Агентства</h1>
        <p>Добро пожаловать, агент {currentUser.username}! <button onClick={logout}>Выйти</button></p>
        <hr />
        
        {view === 'dashboard' && (
          <div>
            <button onClick={() => setView('addCase')}>Создать дело</button> {/* <-- Новая кнопка */}
            <button onClick={() => setView('addNote')}>Добавить заметку</button>
            <button onClick={() => setView('viewNotes')}>Все заметки</button>
          </div>
        )}

        {view === 'addCase' && (
          <div>
            <CaseForm onCaseAdded={() => setView('dashboard')} />
            <button onClick={() => setView('dashboard')}>Назад</button>
          </div>
        )}

        {view === 'addNote' && (
          <div>
            <NoteForm onNoteAdded={() => setView('viewNotes')} />
            <button onClick={() => setView('dashboard')}>Назад</button>
          </div>
        )}

        {view === 'viewNotes' && (
          <div>
            <NoteList />
            <button onClick={() => setView('dashboard')}>Назад</button>
          </div>
        )}

      </header>
    </div>
  );
}

export default App;