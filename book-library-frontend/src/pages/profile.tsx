import React, { useEffect, useState } from 'react';
import axios from 'axios';

type User = {
    id: number;
    name: string;
    email: string;
};

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    availableCopies: number;
    status: boolean;
};

export default function ProfilePage() {
    const [user, setUser] = useState<User | null>(null);
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(true);
    const [bookLoading, setBookLoading] = useState(true);
    const [error, setError] = useState('');
    const [bookError, setBookError] = useState('');

    // Получаем данные пользователя
    useEffect(() => {
        axios.get('http://localhost:8081/api/users/me', {
            withCredentials: true,
        })
            .then(res => {
                setUser(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setError('Не удалось загрузить данные пользователя');
                setLoading(false);
            });
    }, []);

    // Получаем книги пользователя
    useEffect(() => {
        axios.get('http://localhost:8081/api/users/my_books', {
            withCredentials: true,
        })
            .then(res => {
                setBooks(res.data);
                setBookLoading(false);
            })
            .catch(err => {
                console.error(err);
                setBookError('Не удалось загрузить ваши книги');
                setBookLoading(false);
            });
    }, []);

    if (loading) return <div className="p-6 text-center">Загрузка данных пользователя...</div>;
    if (error) return <div className="p-6 text-center text-red-600">{error}</div>;

    return (
        <div className="min-h-screen bg-gray-100 p-8">
            <div className="max-w-3xl mx-auto">
                <div className="bg-white p-6 rounded shadow mb-8 text-center">
                    <h1 className="text-2xl font-bold mb-2">Личный кабинет</h1>
                    <p className="text-lg">Привет, <strong>{user?.name}</strong>!</p>
                    <p className="text-gray-600">{user?.email}</p>
                </div>

                <div className="bg-white p-6 rounded shadow">
                    <h2 className="text-xl font-semibold mb-4">Ваши книги</h2>

                    {bookLoading && <p>Загрузка книг...</p>}
                    {bookError && <p className="text-red-600">{bookError}</p>}
                    {!bookLoading && books.length === 0 && <p>У вас нет взятых книг.</p>}

                    <ul className="space-y-4">
                        {books.map(book => (
                            <li key={book.id} className="border rounded p-4 shadow-sm">
                                <h3 className="text-lg font-bold">{book.title}</h3>
                                <p className="text-sm text-gray-700">Автор: {book.author}</p>
                                <p className="text-sm text-gray-500 mt-1">{book.description}</p>
                                <p className="text-sm mt-2">
                                    Статус: {book.status ? "Доступна" : "Выдана"} | Копий: {book.availableCopies}
                                </p>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}
