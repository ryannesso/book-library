import React, { useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/router';

export default function SignPage() {
    const router = useRouter();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [result, setResult] = useState('');

    const handleRegister = async () => {
        try {
            const res = await axios.post(`http://localhost:8081/api/auth/register`, {
                name,
                email,
                password,
            });
            setResult('User registered successfully!');
        } catch (error) {
            setResult('Registration failed');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white shadow-md rounded px-8 py-6 w-full max-w-md">
                <h2 className="text-2xl font-bold mb-6 text-center">Sign Up</h2>

                <input
                    type="text"
                    placeholder="Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="w-full mb-4 px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-yellow-400"
                />

                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full mb-4 px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-yellow-400"
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full mb-4 px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-yellow-400"
                />

                <button
                    onClick={handleRegister}
                    className="w-full bg-yellow-400 hover:bg-yellow-500 text-white font-semibold py-2 px-4 rounded mb-3 transition"
                >
                    Sign Up
                </button>

                <button
                    onClick={() => router.push('/page')}
                    className="w-full bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-4 rounded transition"
                >
                    Back to Home
                </button>

                {result && <p className="mt-4 text-center text-sm text-red-500">{result}</p>}
            </div>
        </div>
    );
}
