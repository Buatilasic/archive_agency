import React, { useState } from 'react';
import axios from 'axios';

// компонент с формой для 'вербовки' нового агента.
const RegistrationForm = () => {
    // заводим 'личное дело' (state) для данных, которые вводит кандидат.
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        passwordHash: ''
    });

    // здесь будем хранить 'служебные уведомления' (ответы сервера) для пользователя.
    const [message, setMessage] = useState(''); 

    // эта функция обновляет 'личное дело' по мере того, как кандидат заполняет анкету.
    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    // когда анкета заполнена, отправляем её в 'штаб' (на бэкенд).
    const handleSubmit = async (e) => {
        // отменяем стандартное поведение, чтобы страница не перезагружалась.
        e.preventDefault();
        // на всякий случай чистим старые уведомления.
        setMessage('');

        try {
            // отправляем данные по защищённому каналу.
            const response = await axios.post('http://localhost:8080/api/auth/register', formData);
             
            // если всё прошло гладко, рапортуем об успехе.
            console.log('пользователь зарегистрирован:', response.data);
            setMessage(`агент ${response.data.username} успешно зарегистрирован!`);

        } catch (error) {
            // если в 'штабе' что-то пошло не так, докладываем об ошибке.
            console.error('ошибка регистрации:', error.response.data);
            setMessage(`ошибка: ${error.response.data}`);
        }
    };

    // отрисовываем саму анкету.
    return (
        <div>
            <h2>Регистрация нового агента</h2>
            {/* привязываем отправку формы к нашей функции handleSubmit */}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Логин: </label>
                    {/* связываем поля ввода с нашим 'личным делом', чтобы всё обновлялось синхронно */}
                    <input
                        type="text"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Email: </label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Пароль: </label>
                    <input
                        type="password"
                        name="passwordHash"
                        value={formData.passwordHash}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit">Зарегистрировать</button>
            </form>
            {/* показываем 'служебное уведомление', только если оно есть. */}
            {message && <p>{message}</p>}
        </div>
    );
};

export default RegistrationForm;