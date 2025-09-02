
import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth(); 

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const result = await login({ username, password });
        if (!result.success) {
            setError(result.message);
        }
    };

    return (
        <div>
            <h2>Вход для агентов</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Логин: </label>
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required />
                </div>
                <div>
                    <label>Пароль: </label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <button type="submit">Войти</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default LoginForm;