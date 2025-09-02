import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

// компонент для просмотра 'личного архива' — всех заметок текущего агента.
const NoteList = () => {
    // здесь будет храниться список всех найденных 'записей' (заметок).
    const [notes, setNotes] = useState([]);
    // узнаём, кто сейчас на смене (какой пользователь активен).
    const { currentUser } = useAuth();

    // этот 'наблюдатель' следит за тем, кто сейчас в системе.
    useEffect(() => {
        // задача — сделать запрос в 'центральный архив' и получить все записи этого агента.
        const fetchNotes = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/notes/my-notes', {
                    withCredentials: true
                });
                // полученные данные подшиваем в наше локальное 'дело' (state).
                setNotes(response.data);
            } catch (error) {
                console.error("не удалось загрузить заметки:", error);
            }
        };

        // отправляем запрос в архив, только если агент на смене.
        if (currentUser) {
            fetchNotes();
        }
        // он срабатывает каждый раз, когда агент 'заступает на смену' или 'уходит'.
    }, [currentUser]);

    // отображаем найденные 'записи' в виде списка.
    return (
        <div>
            <h2>Все ваши заметки</h2>
            {/* если в личном архиве пусто, сообщаем об этом. */}
            {notes.length === 0 ? (
                <p>У вас пока нет заметок.</p>
            ) : (
                // если записи есть, выводим каждую на отдельном 'бланке'.
                <ul>
                    {notes.map(note => (
                        <li key={note.id}>
                            <strong>{note.title}</strong> (Дело: {note.caseCasename})
                            <p>{note.description}</p>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default NoteList;