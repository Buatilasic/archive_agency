import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

// форма для входа в систему, наш 'пропускной пункт'.
const LoginForm = () => {
    // готовим поля для 'позывного' (логина), 'пароля' и для записи 'рапорта об ошибке'.
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    // получаем доступ к 'служебной рации' (функции login) из нашего общего контекста.
    const { login } = useAuth(); 

    // обработчик 'предъявления документов' (отправки формы).
    const handleSubmit = async (e) => {
        e.preventDefault();
        // сначала очищаем старые рапорты.
        setError('');
        // отправляем 'позывной' и 'пароль' на проверку в 'штаб' через нашу рацию.
        const result = await login({ username, password });
        // если из 'штаба' пришёл ответ 'отказано в доступе'...
        if (!result.success) {
            // ...то фиксируем причину отказа в рапорте.
            setError(result.message);
        }
    };

    return (
        <div>
            <h2>Вход для агентов</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Логин: </label>
                    {/* фиксируем каждое изменение в поле 'позывной' в нашем state */}
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required />
                </div>
                <div>
                    <label>Пароль: </label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <button type="submit">Войти</button>
            </form>
            {/* показываем 'рапорт об ошибке' красным цветом, если он есть. */}
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default LoginForm;