import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/router';

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
    takenByUser: boolean;
};

export default function ProfilePage() {
    const router = useRouter();

    const [user, setUser] = useState<User | null>(null);
    const [books, setBooks] = useState<Book[]>([]);
    const [loadingUser, setLoadingUser] = useState(true);
    const [loadingBooks, setLoadingBooks] = useState(true);
    const [userError, setUserError] = useState('');
    const [booksError, setBooksError] = useState('');
    const [returningBookId, setReturningBookId] = useState<number | null>(null);

    useEffect(() => {
        axios.get('http://localhost:8081/api/users/me', { withCredentials: true })
            .then(res => setUser(res.data))
            .catch(() => setUserError('Failed to load user data'))
            .finally(() => setLoadingUser(false));
    }, []);

    useEffect(() => {
        axios.get('http://localhost:8081/api/users/my_books', { withCredentials: true })
            .then(res => setBooks(res.data))
            .catch(() => setBooksError('Failed to load your books'))
            .finally(() => setLoadingBooks(false));
    }, []);

    const handleReturn = async (bookId: number) => {
        setReturningBookId(bookId);
        try {
            await axios.put('http://localhost:8081/api/books/return', { bookId }, { withCredentials: true });
            setBooks(prevBooks => prevBooks.filter(book => book.id !== bookId));
        } catch (err) {
            console.error('Return failed', err);
            alert('Failed to return the book');
        } finally {
            setReturningBookId(null);
        }
    };

    if (loadingUser) return <div className="p-6 text-center">Loading user data...</div>;
    if (userError) return <div className="p-6 text-center text-red-600">{userError}</div>;

    return (
        <div className="min-h-screen bg-gray-100 p-8">
            <div className="max-w-3xl mx-auto">
                <section className="bg-white p-6 rounded shadow mb-8 text-center">
                    <h1 className="text-2xl font-bold mb-2">Profile</h1>
                    <p className="text-lg">Hello, <strong>{user?.name}</strong>!</p>
                    <p className="text-gray-600">{user?.email}</p>
                    <button
                        onClick={() => router.push('/page')}
                        className="mt-4 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                    >
                        Go to Main Page
                    </button>
                </section>

                <section className="bg-white p-6 rounded shadow">
                    <h2 className="text-xl font-semibold mb-4">Your Borrowed Books</h2>

                    {loadingBooks && <p>Loading books...</p>}
                    {booksError && <p className="text-red-600">{booksError}</p>}
                    {!loadingBooks && books.length === 0 && <p>You have no borrowed books.</p>}

                    <ul className="space-y-4">
                        {books.map(book => (
                            <li key={book.id} className="border rounded p-4 shadow-sm">
                                <h3 className="text-lg font-bold">{book.title}</h3>
                                <p className="text-sm text-gray-700">Author: {book.author}</p>
                                <p className="text-sm text-gray-500 mt-1">{book.description}</p>
                                <p className="text-sm mt-2">
                                    Copies available: {book.availableCopies}
                                </p>
                                <button
                                    onClick={() => handleReturn(book.id)}
                                    disabled={returningBookId === book.id}
                                    className="mt-3 inline-block px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {returningBookId === book.id ? "Returning..." : "Return"}
                                </button>
                            </li>
                        ))}
                    </ul>
                </section>
            </div>
        </div>
    );
}
