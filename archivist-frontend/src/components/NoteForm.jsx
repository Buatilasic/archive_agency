import React, { useState, useEffect } from 'react';
import axios from 'axios';

// компонент-бланк для добавления улик и заметок в дело.
// onNoteAdded — это 'сигнальный маячок' для родительского компонента, чтобы он знал, когда обновить список.
const NoteForm = ({ onNoteAdded }) => {
    // готовим 'блокноты' (state) для всей информации по новой заметке.
    const [cases, setCases] = useState([]); // список всех активных дел.
    const [selectedCaseId, setSelectedCaseId] = useState(''); // номер папки (id), в которую пойдёт заметка.
    const [title, setTitle] = useState(''); // заголовок.
    const [description, setDescription] = useState(''); // содержание.
    const [type, setType] = useState('EVIDENCE'); // тип записи.
    const [message, setMessage] = useState(''); // 'служебное уведомление' о результате.

    // этот эффект — наш 'разведчик', он срабатывает один раз при загрузке компонента.
    useEffect(() => {
        // его задача — запросить в 'архиве' (на бэкенде) список всех дел.
        const fetchCases = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/cases', {
                    // обязательно предъявляем удостоверение (куки) для доступа.
                    withCredentials: true 
                });
                setCases(response.data);
                // если дела есть, по умолчанию выбираем самое первое в списке.
                if (response.data.length > 0) {
                    setSelectedCaseId(response.data[0].id);
                }
            } catch (error) {
                console.error("не удалось загрузить дела:", error);
                setMessage("ошибка: не удалось загрузить список дел.");
            }
        };

        fetchCases();
    }, []); // пустой массив означает, что 'разведчик' выходит на задание лишь однажды.

    // 'протокол отправки': что делать, когда нажимают кнопку 'добавить'.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        // собираем все данные в один 'рапорт' (объект) для отправки.
        const noteData = { title, description, type };

        try {
            // отправляем 'рапорт' в нужный отдел архива (на эндпоинт .../cases/id/notes).
            await axios.post(`http://localhost:8080/api/cases/${selectedCaseId}/notes`, noteData, {
                withCredentials: true
            });
            setMessage('заметка успешно добавлена!');
            
            // после успешной отправки очищаем бланк для следующей записи.
            setTitle('');
            setDescription('');
            setType('EVIDENCE');

            // и подаём 'сигнал наверх', что заметка добавлена (чтобы, например, обновить список).
            if (onNoteAdded) {
                onNoteAdded();
            }
        } catch (error) {
            console.error('ошибка добавления заметки:', error);
            setMessage(`ошибка: ${error.response?.data || 'не удалось добавить заметку'}`);
        }
    };

    // а это сам 'бланк', который видит агент.
    return (
        <div>
            <h2>Добавить новую заметку</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Дело:</label>
                    {/* выпадающий список, который мы заполнили с помощью 'разведчика'. */}
                    <select value={selectedCaseId} onChange={(e) => setSelectedCaseId(e.target.value)} required>
                        {cases.length === 0 ? (
                            <option>Сначала создайте дело</option>
                        ) : (
                            cases.map(c => (
                                <option key={c.id} value={c.id}>
                                    {c.casename}
                                </option>
                            ))
                        )}
                    </select>
                </div>
                <div>
                    <label>Тип заметки:</label>
                    <select value={type} onChange={(e) => setType(e.target.value)} required>
                        <option value="CHARACTER">Персонаж</option>
                        <option value="LOCATION">Локация</option>
                        <option value="EVIDENCE">Улика</option>
                        <option value="THEORY">Теория</option>
                        <option value="EVENT">Событие</option>
                    </select>
                </div>
                <div>
                    <label>Заголовок:</label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Описание (поддерживает Markdown):</label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows="5"
                    />
                </div>
                {/* кнопка 'добавить' неактивна, пока не выбрана папка (дело). */}
                <button type="submit" disabled={!selectedCaseId}>Добавить заметку</button>
            </form>
            {/* показываем 'служебное уведомление', если оно есть. */}
            {message && <p>{message}</p>}
        </div>
    );
};

export default NoteForm; 