import React from 'react';
// Пример иконок из разных наборов
import { FaFolderPlus, FaStickyNote, FaBookOpen, FaArrowLeft } from 'react-icons/fa';

const Navigation = ({ view, setView }) => {
  // Стили для иконок, чтобы они хорошо смотрелись в кнопках
  const iconStyle = { marginRight: '8px', verticalAlign: 'middle' };

  if (view !== 'dashboard') {
    return (
      <div style={{ marginBottom: '20px' }}>
        <button className="btn btn-secondary" onClick={() => setView('dashboard')}>
          <FaArrowLeft style={iconStyle} /> Назад на панель
        </button>
      </div>
    );
  }

  return (
    <div className="dashboard-nav">
      <button className="btn" onClick={() => setView('addCase')}>
        <FaFolderPlus style={iconStyle} /> Создать дело
      </button>
      <button className="btn" onClick={() => setView('addNote')}>
        <FaStickyNote style={iconStyle} /> Добавить заметку
      </button>
      <button className="btn" onClick={() => setView('viewNotes')}>
        <FaBookOpen style={iconStyle} /> Все заметки
      </button>
    </div>
  );
};

export default Navigation;