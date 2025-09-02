import React, { useState } from 'react';
import axios from 'axios';

const RegistrationForm = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        passwordHash: ''
    });

    const [message, setMessage] = useState(''); 

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', formData);
            
            console.log('Пользователь зарегистрирован:', response.data);
            setMessage(`Агент ${response.data.username} успешно зарегистрирован!`);

        } catch (error) {
            console.error('Ошибка регистрации:', error.response.data);
            setMessage(`Ошибка: ${error.response.data}`);
        }
    };

    return (
        <div>
            <h2>Регистрация нового агента</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Логин: </label>
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
            {message && <p>{message}</p>}
        </div>
    );
};

export default RegistrationForm;