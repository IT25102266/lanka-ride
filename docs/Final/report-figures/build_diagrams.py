"""Academic diagrams for the Lanka Ride final report."""
from pathlib import Path

OUT = Path(__file__).resolve().parent


def actor(x, y, labels):
    parts = [
        f'<circle cx="{x}" cy="{y}" r="13" fill="white" stroke="#1b3a4b" stroke-width="1.6"/>',
        f'<line x1="{x}" y1="{y+13}" x2="{x}" y2="{y+44}" stroke="#1b3a4b" stroke-width="1.6"/>',
        f'<line x1="{x-16}" y1="{y+24}" x2="{x+16}" y2="{y+24}" stroke="#1b3a4b" stroke-width="1.6"/>',
        f'<line x1="{x}" y1="{y+44}" x2="{x-12}" y2="{y+64}" stroke="#1b3a4b" stroke-width="1.6"/>',
        f'<line x1="{x}" y1="{y+44}" x2="{x+12}" y2="{y+64}" stroke="#1b3a4b" stroke-width="1.6"/>',
    ]
    ty = y + 82
    for i, line in enumerate(labels):
        parts.append(
            f'<text x="{x}" y="{ty + i*14}" text-anchor="middle" font-family="Times New Roman, serif" font-size="13" fill="#1b3a4b">{line}</text>'
        )
    return "\n".join(parts)


def ellipse(cx, cy, text, w=118, h=32):
    lines = text.split("\n")
    body = [
        f'<ellipse cx="{cx}" cy="{cy}" rx="{w}" ry="{h}" fill="#f7f4ee" stroke="#1b3a4b" stroke-width="1.4"/>'
    ]
    start = cy - (len(lines) - 1) * 7
    for i, line in enumerate(lines):
        body.append(
            f'<text x="{cx}" y="{start + i*14}" text-anchor="middle" font-family="Times New Roman, serif" font-size="13" fill="#1b3a4b">{line}</text>'
        )
    return "\n".join(body)


def line(x1, y1, x2, y2):
    return f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="#5c6b73" stroke-width="1"/>'


def actor_inline(x, y, label):
    return "\n".join([
        f'<circle cx="{x}" cy="{y-16}" r="11" fill="white" stroke="#1b3a4b" stroke-width="1.5"/>',
        f'<line x1="{x}" y1="{y-5}" x2="{x}" y2="{y+16}" stroke="#1b3a4b" stroke-width="1.5"/>',
        f'<line x1="{x-12}" y1="{y+2}" x2="{x+12}" y2="{y+2}" stroke="#1b3a4b" stroke-width="1.5"/>',
        f'<line x1="{x}" y1="{y+16}" x2="{x-9}" y2="{y+32}" stroke="#1b3a4b" stroke-width="1.5"/>',
        f'<line x1="{x}" y1="{y+16}" x2="{x+9}" y2="{y+32}" stroke="#1b3a4b" stroke-width="1.5"/>',
        f'<text x="{x+28}" y="{y+4}" font-family="Times New Roman, serif" font-size="13" fill="#1b3a4b">{label}</text>',
    ])


def usecase():
    ys = [150, 290, 430, 570, 710, 850]
    left = [
        (300, ys[0], "Sign in / register"),
        (300, ys[1], "Reset password"),
        (300, ys[2], "Search vehicles"),
        (300, ys[3], "Request booking"),
        (300, ys[4], "Pay and view invoice"),
        (300, ys[5], "Raise support ticket"),
    ]
    right = [
        (560, ys[0], "Approve or deny\nbooking", "Booking supervisor"),
        (560, ys[1], "Manage vehicles", "Fleet coordinator"),
        (560, ys[2], "Record maintenance", "Fleet coordinator"),
        (560, ys[3], "View reports", "Finance manager"),
        (560, ys[4], "Monitor bookings", "Operations manager"),
        (560, ys[5], "Administer users\nand branches", "Administrator"),
    ]
    svg = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<svg xmlns="http://www.w3.org/2000/svg" width="1040" height="1040" viewBox="0 0 1040 1040">',
        '<rect width="1040" height="1040" fill="white"/>',
        '<rect x="150" y="40" width="530" height="960" rx="8" fill="none" stroke="#1b3a4b" stroke-width="1.6"/>',
        '<text x="415" y="68" text-anchor="middle" font-family="Times New Roman, serif" font-size="16" font-weight="bold" fill="#1b3a4b">Lanka Ride</text>',
    ]
    for _cx, cy, _label in left:
        svg.append(line(78, 520, 188, cy))
    for _cx, cy, _label, _who in right:
        svg.append(line(672, cy, 748, cy))
    for cx, cy, label in left:
        svg.append(ellipse(cx, cy, label, w=112, h=32))
    for cx, cy, label, who in right:
        svg.append(ellipse(cx, cy, label, w=112, h=32))
        svg.append(actor_inline(770, cy, who))
    svg.append(actor(48, 430, ["Customer"]))
    svg.append("</svg>")
    (OUT / "fig-usecase.svg").write_text("\n".join(svg), encoding="utf-8")


def box(x, y, w, h, title, lines):
    parts = [
        f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="4" fill="#f7f4ee" stroke="#1b3a4b" stroke-width="1.3"/>',
        f'<rect x="{x}" y="{y}" width="{w}" height="22" fill="#1b3a4b"/>',
        f'<text x="{x + w/2}" y="{y+15}" text-anchor="middle" font-family="Times New Roman, serif" font-size="12" font-weight="bold" fill="white">{title}</text>',
    ]
    for i, line_text in enumerate(lines):
        parts.append(
            f'<text x="{x+8}" y="{y+38+i*14}" font-family="Times New Roman, serif" font-size="11" fill="#1b3a4b">{line_text}</text>'
        )
    return "\n".join(parts)


def architecture():
    layers = [
        (280, 20, "Web browser", "Customer and staff clients"),
        (280, 95, "Presentation", "JSP views, Bootstrap 5, security tags"),
        (280, 170, "Controllers", "Spring MVC request mapping and validation"),
        (280, 245, "Services", "Vehicle, booking, payment, fleet, reports, support"),
        (280, 320, "Persistence", "Spring Data JPA and Hibernate 6"),
        (280, 395, "MySQL 8", "Database lanka_ride on localhost:3306"),
    ]
    svg = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<svg xmlns="http://www.w3.org/2000/svg" width="980" height="500" viewBox="0 0 980 500">',
        '<rect width="980" height="500" fill="white"/>',
    ]
    for i, (x, y, title, sub) in enumerate(layers):
        svg.append(box(x, y, 420, 58, title, [sub]))
        if i < len(layers) - 1:
            svg.append(f'<line x1="490" y1="{y+58}" x2="490" y2="{layers[i+1][1]}" stroke="#1b3a4b" stroke-width="1.4" marker-end="url(#arr)"/>')
    svg.insert(2, '<defs><marker id="arr" markerWidth="8" markerHeight="8" refX="4" refY="4" orient="auto"><path d="M0,0 L8,4 L0,8 z" fill="#1b3a4b"/></marker></defs>')
    svg.append(box(730, 170, 220, 130, "Spring Security", [
        "Form login",
        "Role-based access",
        "BCrypt passwords",
        "Method security",
    ]))
    svg.append('<line x1="700" y1="235" x2="730" y2="235" stroke="#1b3a4b" stroke-width="1.3"/>')
    svg.append("</svg>")
    (OUT / "fig-architecture.svg").write_text("\n".join(svg), encoding="utf-8")


def rel(x1, y1, x2, y2, label):
    mx, my = (x1 + x2) / 2, (y1 + y2) / 2
    return "\n".join([
        f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="#1b3a4b" stroke-width="1.3"/>',
        f'<rect x="{mx-28}" y="{my-10}" width="56" height="16" fill="white"/>',
        f'<text x="{mx}" y="{my+3}" text-anchor="middle" font-family="Times New Roman, serif" font-size="11" fill="#1b3a4b">{label}</text>',
    ])


def er():
    # (x, y, title, lines) — heights follow the line count
    entities = {
        "users": (300, 36, ["PK id", "username, email", "password hash", "full name"]),
        "roles": (620, 36, ["PK id", "role name"]),
        "branches": (40, 36, ["PK id", "name", "address"]),
        "vehicles": (40, 280, ["PK id", "registration number", "rate and deposit", "FK branch, status"]),
        "bookings": (320, 260, ["PK id", "FK customer", "FK vehicle", "dates and status"]),
        "payments": (630, 280, ["PK id", "FK booking", "type and amount", "actor, reference"]),
        "maintenance": (40, 620, ["PK id", "FK vehicle", "type, cost, status"]),
        "transfers": (270, 640, ["PK id", "FK vehicle", "from and to branch"]),
        "tickets": (520, 640, ["PK id", "FK customer_id", "subject, status"]),
        "tokens": (740, 640, ["PK id", "FK user_id", "token, expiry"]),
    }
    width = 200

    def rect(key):
        x, y, lines = entities[key]
        h = 26 + len(lines) * 15
        return x, y, width, h

    svg = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<svg xmlns="http://www.w3.org/2000/svg" width="980" height="900" viewBox="0 0 980 900">',
        '<rect width="980" height="900" fill="white"/>',
    ]
    # Relationship lines first, so the boxes cover the joins.
    links = [
        ("branches", "s", "vehicles", "n", "1 : N"),
        ("vehicles", "s", "maintenance", "n", "1 : N"),
        ("vehicles", "e", "bookings", "w", "1 : N"),
        ("users", "s", "bookings", "n", "1 : N"),
        ("users", "e", "roles", "w", "M : N"),
        ("bookings", "e", "payments", "w", "1 : N"),
    ]
    for a, sa, b, sb, label in links:
        ax, ay, aw, ah = rect(a)
        bx, by, bw, bh = rect(b)
        points = {
            "n": lambda x, y, w, h: (x + w / 2, y),
            "s": lambda x, y, w, h: (x + w / 2, y + h),
            "e": lambda x, y, w, h: (x + w, y + h / 2),
            "w": lambda x, y, w, h: (x, y + h / 2),
        }
        x1, y1 = points[sa](ax, ay, aw, ah)
        x2, y2 = points[sb](bx, by, bw, bh)
        svg.append(rel(x1, y1, x2, y2, label))
    for key, (x, y, lines) in entities.items():
        h = 26 + len(lines) * 15
        svg.append(box(x, y, width, h, key, lines))
    svg.append(
        '<text x="40" y="860" font-family="Times New Roman, serif" font-size="13" fill="#1b3a4b">'
        "Tickets and password-reset tokens belong to a user. Branch transfers belong to a vehicle. user_roles joins users and roles.</text>"
    )
    svg.append("</svg>")
    (OUT / "fig-er.svg").write_text("\n".join(svg), encoding="utf-8")


if __name__ == "__main__":
    usecase()
    architecture()
    er()
    print("diagrams written")
