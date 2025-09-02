import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import './NoteList.css'; // <-- 1. Импортируем файл со стилями

const NoteList = () => {
    const [notes, setNotes] = useState([]);
    const { currentUser } = useAuth();

    useEffect(() => {
        const fetchNotes = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/notes/my-notes');
                setNotes(response.data);
            } catch (error) {
                console.error("Не удалось загрузить заметки:", error);
            }
        };

        if (currentUser) {
            fetchNotes();
        }
    }, [currentUser]);

    return (
       // ...
<div className="note-grid">
    {notes.map(note => (
        <div key={note.id} className="note-card">
            {/* Блок с заголовком */}
            <div>
                <ReactMarkdown components={{ p: 'h3' }} remarkPlugins={[remarkGfm]}>
                    {note.title}
                </ReactMarkdown>
                <small>(Дело: {note.caseCasename})</small>
            </div>

            {/* Блок с контентом */}
            <div className="card-content">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {note.description}
                </ReactMarkdown>
            </div>

            {/* НОВОЕ: "Подвал" карточки с типом заметки */}
            <div className="note-footer">
                <span className="note-type-badge">{note.type}</span>
            </div>
        </div>
    ))}
</div>
// ...
    );
};

export default NoteList;