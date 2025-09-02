// src/App.js

import React from 'react';
import { useAuth } from './context/AuthContext';
import LoginForm from './components/LoginForm';
import RegistrationForm from './components/RegistrationForm';
import './App.css';

function App() {
  const { currentUser, logout } = useAuth();

  return (
    <div className="App">
      <header className="App-header">
        <h1>Архив Детективного Агентства</h1>
        
        {currentUser ? (
          // Если агент вошёл в систему
          <div>
            <p>Добро пожаловать, агент {currentUser.username}!</p>
            <button onClick={logout}>Выйти</button>
          </div>
        ) : (
          <div>
            <LoginForm />
            <hr />
            <RegistrationForm />
          </div>
        )}
      </header>
    </div>
  );
}

export default App;