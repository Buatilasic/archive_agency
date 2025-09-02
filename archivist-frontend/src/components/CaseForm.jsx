import React, { useState } from 'react';
import axios from 'axios';

// компонент-бланк для заведения нового 'дела' в архив.
// onCaseAdded — это сигнал для 'штаба', что пора возвращаться на главный экран.
const CaseForm = ({ onCaseAdded }) => {
    // заводим 'папку' (state) для названия, описания и служебных сообщений.
    const [casename, setCasename] = useState('');
    const [casedescription, setCasedescription] = useState('');
    const [message, setMessage] = useState('');

    // протокол 'открытия дела', который запускается при отправке формы.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        // собираем все материалы в одно досье для отправки в архив.
        const caseData = { casename, casedescription };

        try {
            // отправляем досье в 'центральный архив' (на бэкенд).
                await axios.post('http://localhost:8080/api/cases', caseData, { withCredentials: true });
                setMessage('дело успешно создано!');
            // после успеха, докладываем 'наверх', чтобы нас вернули на приборную панель.
            if (onCaseAdded) {
                onCaseAdded();
            }
        } catch (error) {
            console.error('ошибка создания дела:', error);
            setMessage(`ошибка: ${error.response?.data || 'не удалось создать дело'}`);
        }
    };

    // сам 'бланк' для заполнения.
    return (
        <div>
            <h2>Создать новое дело</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Название дела:</label>
                    {/* держим поля бланка и данные в 'папке' синхронизированными. */}
                    <input
                        type="text"
                        value={casename}
                        onChange={(e) => setCasename(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Описание:</label>
                    <textarea
                        value={casedescription}
                        onChange={(e) => setCasedescription(e.target.value)}
                        rows="4"
                    />
                </div>
                <button type="submit">Создать дело</button>
            </form>
            {/* выводим 'служебное сообщение' по итогам операции. */}
            {message && <p>{message}</p>}
        </div>
    );
};

export default CaseForm;