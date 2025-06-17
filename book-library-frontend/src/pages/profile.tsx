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
            .then(res => {
                setUser(res.data);
            })
            .catch(() => {
                setUserError('Failed to load user data. Please log in again.');
                router.push('/login_page'); // Redirect to login if user data fails to load
            })
            .finally(() => setLoadingUser(false));
    }, []);

    useEffect(() => {
        // Only fetch books if user is successfully loaded and not in a loading state
        if (!user && !loadingUser) {
            return;
        }

        axios.get('http://localhost:8081/api/users/my_books', { withCredentials: true })
            .then(res => setBooks(res.data))
            .catch(() => setBooksError('Failed to load your books'))
            .finally(() => setLoadingBooks(false));
    }, [user, loadingUser]); // Depend on user and loadingUser state

    const handleReturn = async (bookId: number) => {
        setReturningBookId(bookId);
        try {
            await axios.put('http://localhost:8081/api/books/return', { bookId }, { withCredentials: true });
            setBooks(prevBooks => prevBooks.filter(book => book.id !== bookId));
            alert('Book successfully returned!');
        } catch (err) {
            console.error('Return failed', err);
            alert('Failed to return the book. Please try again.');
        } finally {
            setReturningBookId(null);
        }
    };

    if (loadingUser) return <div className="min-h-screen flex items-center justify-center bg-background-primary text-text-light text-xl">Loading user data...</div>;
    if (userError) return <div className="min-h-screen flex items-center justify-center bg-background-primary text-accent-danger text-xl">{userError}</div>;

    return (
        <div className="min-h-screen bg-background-primary p-8">
            <div className="max-w-4xl mx-auto">
                <section className="card-base p-10 mb-10 text-center">
                    <h1 className="text-4xl font-extrabold mb-4 text-text-light">Your Profile</h1>
                    <p className="text-xl text-text-light mb-1">Hello, <strong className="text-accent-primary">{user?.name}</strong>!</p>
                    <p className="text-text-muted text-lg">{user?.email}</p>
                    <button
                        onClick={() => router.push('/page')}
                        className="mt-8 px-8 py-3 btn-primary text-lg"
                    >
                        Go to Main Page
                    </button>
                </section>

                <section className="card-base p-10">
                    <h2 className="text-3xl font-bold mb-8 text-text-light text-center">Your Borrowed Books</h2>

                    {loadingBooks && <p className="text-text-muted text-center text-lg">Loading your books...</p>}
                    {booksError && <p className="text-accent-danger text-center text-lg">{booksError}</p>}
                    {!loadingBooks && books.length === 0 && <p className="text-text-muted text-center text-lg">You currently have no borrowed books.</p>}

                    <ul className="space-y-6">
                        {books.map(book => (
                            <li key={book.id} className="card-base p-6 transition-all duration-300 hover:shadow-xl hover:border-accent-primary transform hover:-translate-y-0.5 flex flex-col sm:flex-row justify-between items-start sm:items-center">
                                <div className="flex-grow mb-4 sm:mb-0">
                                    <h3 className="text-2xl font-bold text-text-light">{book.title}</h3>
                                    <p className="text-base text-text-muted mt-1">Author: {book.author}</p>
                                    <p className="text-sm text-text-muted mt-2 max-w-lg line-clamp-2">{book.description}</p>
                                </div>
                                <button
                                    onClick={() => handleReturn(book.id)}
                                    disabled={returningBookId === book.id}
                                    className="btn-danger px-6 py-2.5 font-medium text-base sm:ml-6"
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