# Eco Action Chat – Campus Cleanup

A Java LAN chat application for campus environmental volunteers. Besides ordinary group chat, it gives volunteers a **shared Cleanup Task Board** and **Collection Reports** so a cleanup drive can be coordinated in one place instead of across scattered messages.

> **Course:** ITAS1 – Network Programming | **Section:** `<BSIT 2B-SD>`
> **Submission:** November 23–27, 2026

---

## 1. Members and GitHub Usernames

| Member | GitHub Username |
|---|---|---|
| `<Jenelyn V. Rodrigo>` | `jnelynvrdrgo` | 
| `<Frexia G. Nueva>` | `nuevafrexia-droid` |

---

## 2. Concept Proposal and Approval

| Item | Details |
|---|---|
| **Theme** | Eco Action Chat |
| **Target audience** | Campus environmental volunteers |
| **User problem** | Cleanup drives are coordinated through ordinary chat, where task assignments and garbage-collection totals get buried in the conversation and nobody has a clear shared picture of who is doing what or how much has been collected. |
| **Original feature 1** | **Cleanup Task Board** – any member can create a task (task, location, assignee) and anyone can mark it done. Status is synchronized to all clients through the server. |
| **Original feature 2** | **Collection Reports** – teams report bags collected, area, and notes. Reports are timestamped and appear in a shared table on every client. |
| **Instructor approval** | Kenth Joshua Espina – `<Approved / Date>` |

---

## 3. Features

### Core features
- TCP server (`ServerSocket`) with one thread per client; clients can join and leave without stopping the server
- Configurable server IP and port in the GUI
- Swing GUI: nickname field, Connect/Disconnect, message input, Send button, conversation area, online members list
- Group chat broadcast with sender nickname and time
- Unique, non-blank nicknames
- Commands: `/name <newname>`, `/exit`, `/help`
- Input validation and error messages for blank messages, invalid IP/port, and unavailable server
- Join/leave/rename updates sent to all clients

### Original theme features
| Feature | What it does | Shared through server? |
|---|---|---|
| Cleanup Task Board | Add a task with location and assignee; mark selected task as DONE; shows Created By and Status | Yes – all clients see the same board |
| Collection Reports | Report bags collected, area, notes; time is recorded | Yes – all clients see the same report list |

---

## 4. Architecture

```
 ┌────────────┐        ┌──────────────────────────┐        ┌────────────┐
 │  Client A  │◄──────►│          Server          │◄──────►│  Client B  │
 │ (Swing GUI)│  TCP   │  ServerSocket :5000      │  TCP   │ (Swing GUI)│
 └────────────┘        │  ClientHandler thread/   │        └────────────┘
                       │  client                  │
 ┌────────────┐        │  Shared state:           │
 │  Client C  │◄──────►│   - online users         │
 │ (Swing GUI)│  TCP   │   - chat history         │
 └────────────┘        │   - task list            │
                       │   - collection reports   │
                       └──────────────────────────┘
```

**Message path:** a client's GUI action → a background network thread writes a line to the socket → the server's `ClientHandler` thread reads and validates it → the server updates shared state (synchronized) → the server broadcasts the result to every connected client → each client's reader thread updates the Swing GUI through `SwingUtilities.invokeLater`.

### Message format

> ⚠️ **Edit this table to match your actual protocol.** The formats below are placeholders; replace them with what your code really sends.

| Purpose | Client → Server | Server → Clients |
|---|---|---|
| Join | `JOIN\|<nickname>` | `USERS\|<n1,n2,...>` and `MSG\|Server\|<time>\|<nick> joined.` |
| Chat | `MSG\|<text>` | `MSG\|<nick>\|<time>\|<text>` |
| Rename | `/name <new>` | `USERS\|...` + system message |
| Add task | `TASK_ADD\|<task>\|<location>\|<assignedTo>` | `TASK\|<task>\|<location>\|<assignedTo>\|<status>\|<createdBy>` |
| Mark done | `TASK_DONE\|<row/id>` | updated `TASK\|...` |
| Report | `REPORT\|<team>\|<bags>\|<area>\|<notes>` | `REPORT\|<team>\|<bags>\|<area>\|<notes>\|<time>` |
| Exit | `/exit` | `USERS\|...` + leave message |

---

## 5. Setup Instructions

| Item | Value |
|---|---|
| Java version | `<e.g., JDK 17>` |
| IDE / build tool | `<e.g., IntelliJ IDEA / NetBeans / Maven>` |
| Dependencies | None (Java standard library, Swing) / `<list any>` |

### Run the server
```bash
cd src
javac *.java
java Server            # listens on port 5000
```

### Run a client
```bash
java Client
```
In the client window, enter a **Nickname**, the **Server IP**, and the **Port** (default `5000`), then click **Connect**.

### Running across two computers (LAN)
1. Connect both computers to the same Wi-Fi/LAN.
2. On the server computer, find its IPv4 address (`ipconfig` on Windows).
3. Start the server there.
4. On the other computer, start the client and enter the server's IPv4 address and port `5000`.
5. If connection fails, allow Java through the server computer's firewall (or open port 5000).

### Troubleshooting
| Problem | Fix |
|---|---|
| "Connection refused" / server unavailable | Make sure the server is running and the IP/port are correct |
| Works on localhost but not on LAN | Check firewall; confirm both computers are on the same network |
| Nickname rejected | Nickname is blank or already used by an active user |
| Port already in use | Close the old server instance or use a different port |

---

## 6. Commands

| Command | Description |
|---|---|
| `/name <newname>` | Change your nickname (must be non-blank and unique) |
| `/exit` | Disconnect cleanly |
| `/help` | Show the list of commands |

---

## 7. Screenshots

### 7.1 Client before connecting (Offline)
Offline status, empty chat area and member list, connection controls ready.

![Client offline](screenshots/01-offline-start.png)

### 7.2 Group Chat after connecting
Status changes to **Connected** and the user appears in **Online Members**; the server announces the join.

![Group chat connected](screenshots/02-group-chat-connected.png)

### 7.3 Group Chat message
A message sent by `Yes-O` appears in the conversation area with the sender nickname and time.

![Group chat message](screenshots/03-group-chat-message.png)

### 7.4 Cleanup Task Board (empty)
![Task board empty](screenshots/04-task-board-empty.png)

### 7.5 Collection Reports (empty)
![Collection reports empty](screenshots/05-collection-reports-empty.png)

### 7.6 Adding a cleanup task
The **New Cleanup Task** dialog asks for Task, Location, and Assigned to.

![New cleanup task dialog](screenshots/06-new-cleanup-task-dialog.png)

### 7.7 Task created – status OPEN
The new task appears on the board with status **OPEN** and the creator's nickname.

![Task board with open task](screenshots/07-task-board-open.png)

### 7.8 Task marked done – status DONE
After **Mark Selected Done**, the status changes to **DONE**.

![Task board with done task](screenshots/08-task-board-done.png)

### 7.9 Reporting a collection
The **Collection Report** dialog asks for Team/Name, Bags collected, Area, and Notes.

![Collection report dialog](screenshots/09-collection-report-dialog.png)

### 7.10 Collection Reports table updated
The report appears in the shared table with the time it was submitted.

![Collection reports filled](screenshots/10-collection-reports-filled.png)

---

## 8. Test Results

Tests marked **Pass** are backed by the screenshots above. Complete the remaining rows by running them and adding screenshots to the `screenshots/` folder.

| # | Test | Steps | Expected | Actual | Status | Evidence |
|---|---|---|---|---|---|---|
| 1 | Client starts offline | Launch client | Status shows Offline, controls visible | As expected | Pass | 7.1 |
| 2 | Connect to server | Enter nickname/IP/port, click Connect | Status Connected, user in online list, join message | As expected | Pass | 7.2 |
| 3 | Send group message | Type message, click Send | Message shows sender nickname and time | As expected | Pass | 7.3 |
| 4 | Add cleanup task | Add Cleanup Task → fill fields → OK | Task appears with OPEN status | As expected | Pass | 7.6, 7.7 |
| 5 | Mark task done | Select task → Mark Selected Done | Status becomes DONE | As expected | Pass | 7.8 |
| 6 | Submit collection report | Report Collection → fill fields → OK | Row appears with time | As expected | Pass | 7.9, 7.10 |
| 7 | Three concurrent clients | Start 3 clients | All connect; online list shows 3 | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 8 | Message broadcast to all clients | Send message from one client | All clients (including sender) receive it once | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 9 | Online list updates | Join / leave / rename | All clients update | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 10 | Valid nickname change | `/name NewName` | Name updated everywhere | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 11 | Duplicate nickname | `/name` to an existing name | Rejected with message | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 12 | Blank message | Click Send with empty input | Rejected, nothing sent | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 13 | Invalid IP / port | Enter bad IP or port, Connect | Helpful error, GUI stays responsive | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 14 | Unavailable server | Connect with server stopped | Error shown, no freeze | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 15 | Clean `/exit` | Type `/exit` | Client disconnects; others notified | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 16 | Abrupt disconnect | Kill one client | Server and other clients continue | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 17 | Task sync across clients | Add/mark task on Client A | Client B sees same state | `<fill>` | `<Pass/Fail>` | `<screenshot>` |
| 18 | Report sync across clients | Report on Client B | Client A sees same report | `<fill>` | `<Pass/Fail>` | `<screenshot>` |

### Known issues
- System and chat lines in Group Chat show literal `\n` characters (for example `[System] Connected.\n[09:04] Server: Yes-O joined.\n`) instead of line breaks. Fix by appending real newlines to the chat area.

---

## 9. Contribution Log

| Date | Member | Task | Commit link |
|---|---|---|
| `<2026-11-26>` | `<Jenelyn V. Rodrigo>` | `<138920aad1631e593685f79dda32eff7fb235c5d>` |
| `<2026-11-26>` | `<Frexia G. Nueva>` | `<>` |
