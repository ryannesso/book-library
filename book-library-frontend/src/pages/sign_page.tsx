import React, { useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/router';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export default function SignPage() {
    const router = useRouter();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [result, setResult] = useState<string | null>(null);
    const [isSuccess, setIsSuccess] = useState(false);

    const handleRegister = async () => {
        setResult(null);
        setIsSuccess(false);
        try {
            const res = await axios.post(`${API_URL}/api/auth/register`, {
                name,
                email,
                password,
            });
            setResult('User registered successfully! You can now log in.');
            setIsSuccess(true);
            setName('');
            setEmail('');
            setPassword('');
        } catch (error: any) {
            if (error.response && error.response.status === 409) {
                setResult('Registration failed: Email already exists.');
            } else {
                setResult('Registration failed. Please try again.');
            }
            setIsSuccess(false);
            console.error("Registration error:", error);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-background-primary p-6">
            <div className="card-base p-10 w-full max-w-md">
                <h2 className="text-4xl font-extrabold mb-8 text-center text-text-light">Sign Up</h2>

                <input
                    type="text"
                    placeholder="Your Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="w-full mb-6"
                />

                <input
                    type="email"
                    placeholder="Your Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full mb-6"
                />

                <input
                    type="password"
                    placeholder="Choose a Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full mb-8"
                />

                <button
                    onClick={handleRegister}
                    className="w-full btn-success py-3.5 mb-4"
                >
                    Sign Up
                </button>

                <button
                    onClick={() => router.push('/page')}
                    className="w-full btn-secondary py-3.5"
                >
                    Back to Home
                </button>

                {result && (
                    <p className={`mt-6 text-center text-lg font-medium ${isSuccess ? 'text-accent-success' : 'text-accent-danger'}`}>
                        {result}
                    </p>
                )}
            </div>
        </div>
    );
}
