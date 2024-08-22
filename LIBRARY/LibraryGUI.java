import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

// Book class
// Modified Book class with a constructor that accepts title, author, and category
class Book {
    private final String title;
    private final String author;
    private String publicationDate;
    private String category;
    private boolean isAvailable;

    public Book(String title, String author, String category) { // Modified constructor
        this.title = title;
        this.author = author;
        this.category = category;
        this.publicationDate = "";
        this.isAvailable = true;
    }

    // Getters and setters for all fields
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublicationDate() { return publicationDate; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
    public void setCategory(String category) { this.category = category; }  // Added setter for category

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author +
                ", Publication Date: " + publicationDate +
                ", Category: " + category +
                ", Available: " + (isAvailable ? "Yes" : "No");
    }
}
// Specialized FictionBook class
class FictionBook extends Book {
    private String genre;

    public FictionBook(String title, String author, String genre) {
        super(title, author, "Fiction");
        this.genre = genre;
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return super.toString() + ", Genre: " + genre;
    }
}
// Specialized NonFictionBook class
class NonFictionBook extends Book {
    private String subject;

    public NonFictionBook(String title, String author, String subject) {
        super(title, author, "Non-Fiction");
        this.subject = subject;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    @Override
    public String toString() {
        return super.toString() + ", Subject: " + subject;
    }
}

// User class
class User {
    private String name;
    private String userID;

    public User(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getUserID() { return userID; }

    @Override
    public String toString() {
        return name + " (" + userID + ")";
    }
}

// Library Management System class
class LibraryManagementSystem {
    private ArrayList<Book> books;
    private ArrayList<User> users;
    private HashMap<User, Book> borrowedBooks;

    public LibraryManagementSystem() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        borrowedBooks = new HashMap<>();
    }

    public void addBook(Book book) { books.add(book); }
    public void removeBook(Book book) { books.remove(book); }
    public void addUser(User user) { users.add(user); }

    //check if a book is available
    public boolean isBookAvailable(Book book) {
        return book.isAvailable() && books.contains(book);
    }

    public boolean borrowBook(User user, Book book) {
        if (book.isAvailable() && users.contains(user)) {
            book.setAvailable(false);
            System.out.println(user.getName() + " borrowed " + book.getTitle());
            return true;
        }else {
            System.out.println("Book is not available.");
            return false;
        }
    }

    public Book returnBook(User user) {
        if (borrowedBooks.containsKey(user)) {
            Book book = borrowedBooks.remove(user);
            book.setAvailable(true);
            return book;
        }
        return null; // No book was returned
    }
    // Search books by criteria
    public ArrayList<Book> searchBooks(String title, String author,String category, String publicationDate, Boolean isAvailable) {
        return books.stream()
                .filter(b -> (title.isEmpty() || b.getTitle().contains(title)) &&
                        (author.isEmpty() || b.getAuthor().contains(author)) &&
                        (publicationDate.isEmpty() || b.getPublicationDate().contains(publicationDate)) &&
                        (category.isEmpty() || b.getCategory().equalsIgnoreCase(category)) &&
                        (isAvailable == null || b.isAvailable() == isAvailable))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // New method to get available books only
    public ArrayList<Book> getAvailableBooks() {
        return books.stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Save books to file
    public void saveBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book book : books) {
                writer.write(book.getTitle() + "|" + book.getAuthor() + "|" + book.getPublicationDate() + "|" + book.getCategory() + "|" + book.isAvailable());
                writer.newLine(); // This is correct because 'writer' is a BufferedWriter
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save users to file
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
            for (User user : users) {
                writer.write(user.getName() + "|" + user.getUserID());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load books from file
    public void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    Book book = new Book(parts[0], parts[1], parts[3]); // parts[3] is category
                    book.setPublicationDate(parts[2]); // Use setter to set publication date
                    book.setAvailable(Boolean.parseBoolean(parts[4]));
                    books.add(book);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<User> getUsers() { return users; }
    public HashMap<User, Book> getBorrowedBooks() { return borrowedBooks; }
}


// Main GUI class integrating both Library and User Management
public class LibraryGUI {
    private LibraryManagementSystem lms;
    private JFrame frame;
    private JTextArea textArea;
    private JTextField bookTitleField;
    private JTextField bookAuthorField;
    private JTextField bookPublicationDateField;
    private JComboBox<String> bookCategoryField;
    private JTextField userNameField;
    private JTextField userIDField;
    private JCheckBox availableCheckBox;
    private JList<String> bookList;  // List to display book titles
    private DefaultListModel<String> listModel;

    public LibraryGUI(LibraryManagementSystem lms) {
        this.lms = lms;
        frame = new JFrame("Library Management System");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 3));

        bookTitleField = new JTextField();
        bookAuthorField = new JTextField();
        bookPublicationDateField = new JTextField();
        bookCategoryField = new JComboBox<>(new String[] {"Fiction", "Non-Fiction", "Science", "Biography"});
        userNameField = new JTextField();
        userIDField = new JTextField();
        availableCheckBox = new JCheckBox("Available");

        panel.add(new JLabel("Book Title:"));
        panel.add(bookTitleField);
        panel.add(new JLabel("Book Author:"));
        panel.add(bookAuthorField);
        panel.add(new JLabel("Publication Date:"));
        panel.add(bookPublicationDateField);
        panel.add(new JLabel("Book Category:"));
        panel.add(bookCategoryField);
        panel.add(new JLabel("User Name:"));
        panel.add(userNameField);
        panel.add(new JLabel("User ID:"));
        panel.add(userIDField);
        panel.add(new JLabel("Available:"));

        JButton addBookButton = new JButton("Add Book");
        JButton addUserButton = new JButton("Add User");
        JButton borrowBookButton = new JButton("Borrow Book");
        JButton returnBookButton = new JButton("Return Book");
        JButton viewAvailableBooksButton = new JButton("View Available Books");
        JButton searchBookButton = new JButton("Search Books");

        panel.add(addBookButton);
        panel.add(addUserButton);
        panel.add(borrowBookButton);
        panel.add(returnBookButton);
        panel.add(viewAvailableBooksButton);
        panel.add(searchBookButton);

        frame.add(panel, BorderLayout.NORTH);

        // Corrected the closing parenthesis and added the closing brace

        addBookButton.addActionListener(e -> {
            String title = bookTitleField.getText();
            String author = bookAuthorField.getText();
            String category = (String) bookCategoryField.getSelectedItem();
            String publicationDate = bookPublicationDateField.getText();

            // Debugging prints
            System.out.println("Add Book button clicked");
            System.out.println("Title: " + title + ", Author: " + author + ", Category: " + category);

            if (!title.isEmpty() && !author.isEmpty() && category != null) {
                lms.addBook(new Book(title, author, category));
                textArea.append("Book added:\n");
                textArea.append("Title: " + title + "\n");
                textArea.append("Author: " + author + "\n");
                textArea.append("Category: " + category + "\n");
                textArea.append("Publication Date: " + publicationDate + "\n");
                textArea.append("--------------------------\n");
                textArea.append("Book added: " + title + "\n");
            } else {
                textArea.append("Error: Please fill in all fields.\n");
            }
        });
        addUserButton.addActionListener(e -> {
            String name = userNameField.getText();
            String userID = userIDField.getText();
            // Debugging prints
            System.out.println("Add User button clicked");
            System.out.println("Name: " + name + ", UserID: " + userID);

            if (!name.isEmpty() && !userID.isEmpty()) {
                lms.addUser(new User(name, userID));
                textArea.append("User added: " + name + "\n");
            } else {
                textArea.append("Error: Please fill in all fields.\n");
            }
        });

        borrowBookButton.addActionListener(e -> {
            String title = bookTitleField.getText();
            String userID = userIDField.getText();
            Book book = lms.getBooks().stream()
                    .filter(b -> b.getTitle().equals(title) && b.isAvailable())
                    .findFirst().orElse(null);
            User user = lms.getUsers().stream()
                    .filter(u -> u.getUserID().equals(userID))
                    .findFirst().orElse(null);
            if (book != null && user != null && lms.borrowBook(user, book)) {
                textArea.append("Book borrowed: " + book.getTitle() + "\n");
            } else {
                textArea.append("Book or user not found, or book unavailable.\n");
            }
        });

        returnBookButton.addActionListener(e -> {
            String userID = userIDField.getText();
            User user = lms.getUsers().stream()
                    .filter(u -> u.getUserID().equals(userID))
                    .findFirst().orElse(null);

            if (user != null) {
                // Attempt to return the book
                Book returnedBook = lms.returnBook(user);
                if (returnedBook != null) {
                    // Get current date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateTime = sdf.format(new Date());

                    // Display return information
                    textArea.append("Book returned:\n");
                    textArea.append("Date and Time: " + currentDateTime + "\n");
                    textArea.append("User Name: " + user.getName() + "\n");
                    textArea.append("User ID: " + user.getUserID() + "\n");
                    textArea.append("Title of the Book: " + returnedBook.getTitle() + "\n");
                } else {
                    textArea.append("No book was borrowed by this user or the book could not be returned.\n");
                }
            } else {
                textArea.append("User not found.\n");
            }
        });
        // ActionListener for "View Available Books" button
        viewAvailableBooksButton.addActionListener(e -> {
            ArrayList<Book> availableBooks = lms.getAvailableBooks();
            if (availableBooks.isEmpty()) {
                textArea.append("No books are currently available.\n");
            } else {
                textArea.append("list all Available Books:\n");
                for (Book book : availableBooks) {
                    textArea.append(book.toString() + "\n");
                }
                textArea.append("--------------------------\n");
            }
        });
        // Add ActionListener for Search Books button
        searchBookButton.addActionListener(e -> {
            textArea.append("[INFO] Searching books...\n");
            String title = bookTitleField.getText().trim();
            String author = bookAuthorField.getText().trim();
            String publicationDate = bookPublicationDateField.getText().trim();
            String category = (String) bookCategoryField.getSelectedItem();
            Boolean isAvailable = availableCheckBox.isSelected(); // check if the checkbox is selected


            // Validation logic
            if (title.isEmpty() && author.isEmpty()) {
                textArea.append("[ERROR] Please enter at least a title or an author to search.\n");
                return;  // Stop further processing
            }

            // Optional: Validate publication date format if it's not empty
            if (!publicationDate.isEmpty()) {
                if (!publicationDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    textArea.append("[ERROR] Invalid publication date format. Use YYYY-MM-DD.\n");
                    return;  // Stop further processing
                }
            }
            // Search books based on criteria, including availability
            ArrayList<Book> results = lms.searchBooks(title, author, category, publicationDate, isAvailable ? true : null);

            if (results.isEmpty()) {
                textArea.append("No books found matching the criteria.\n");
            } else {
                textArea.append("Search Results:\n");
                for (Book book : results) {
                    // Check if the title or author matches the user's input
                    boolean titleMatches = title.isEmpty() || book.getTitle().equalsIgnoreCase(title);
                    boolean authorMatches = author.isEmpty() || book.getAuthor().equalsIgnoreCase(author);

                    if (titleMatches || authorMatches) {
                        textArea.append("Title: " + book.getTitle() + "\n");
                        textArea.append("Author: " + book.getAuthor() + "\n");
                        textArea.append("Publication Date: " + book.getPublicationDate() + "\n");
                        textArea.append("Category: " + book.getCategory() + "\n");
                        textArea.append("Available: " + (book.isAvailable() ? "Yes" : "No") + "\n");
                        textArea.append("--------------------------\n");
                    }
                }
            }
        });


        frame.setVisible(true);
    }

    public static void main(String[] args) {
        LibraryManagementSystem lms = new LibraryManagementSystem();
        new LibraryGUI(lms);
    }
}
