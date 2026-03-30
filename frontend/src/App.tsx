import { useEffect, useState } from "react";

const profile = {
  name: "동그라미분식",
  description: "맛있는 분식",
  location: "서울특별시 중구 남대문로 36",
  heroImage: "/images/store/exterior.png",
  ceo: "이춘화"
};

const businessHours = [
  { day: "일요일", hours: "오후 1:30 - 오후 9:00" },
  { day: "월요일", hours: "휴무" },
  { day: "화요일", hours: "휴무" },
  { day: "수요일", hours: "휴무" },
  { day: "목요일", hours: "휴무" },
  { day: "금요일", hours: "휴무" },
  { day: "토요일", hours: "오후 1:30 - 오후 9:00" }
];

const menuItems = [
  {
    name: "떡볶이",
    category: "분식",
    price: "3,500원",
    description: "동그라미분식의 대표 메뉴. 매콤하게 즐기는 기본 분식 한 접시.",
    image: "/images/menu/tteokbokki.png"
  },
  {
    name: "어묵",
    category: "분식",
    price: "3,000원",
    description: "따뜻한 국물과 함께 편하게 즐기는 기본 어묵",
    image: "/images/menu/eomuk.png"
  },
  {
    name: "순대",
    category: "분식",
    price: "4,500원",
    description: "쫄깃한 식감과 담백한 맛으로 떡볶이와 잘 어울리는 순대",
    image: "/images/menu/sundae.png"
  },
  {
    name: "신메뉴 개발 중",
    category: "준비 중",
    price: "업데이트 예정",
    description: "맛있는 메뉴를 준비하고 있습니다.",
    image: "/images/menu/coming_soon.png"
  }
];

function App() {
  const [showTopButton, setShowTopButton] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setShowTopButton(window.scrollY > 24);
    };

    handleScroll();
    window.addEventListener("scroll", handleScroll, { passive: true });

    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, []);

  return (
    <div id="top" className="page-shell">
      <header className="hero">
        <div className="hero__content">
          <h1>{profile.name}</h1>
          <div className="hero__story">
            <p className="hero__story-body">
              동그라미분식은 빠르게 먹고 가는 한 끼 안에도 오래 남는 기분이 있다고
              믿습니다. 붉게 끓는 떡볶이 냄비와 바삭한 튀김을 고르던 망설임처럼,
              익숙한 맛의 기억이 천천히 다시 돌아오도록 작은 분식집의 온도를
              담았습니다.
            </p>
            <div className="fact-grid">
              <article className="fact-card">
                <span>기억의 맛</span>
                <strong>매콤하고 포근한 한 접시</strong>
              </article>
              <article className="fact-card">
                <span>가게의 리듬</span>
                <strong>천천히 끓이고, 금방 친숙해지는 분위기</strong>
              </article>
              <article className="fact-card">
                <span>동그라미의 의미</span>
                <strong>돌고 돌아 다시 찾게 되는 분식집</strong>
              </article>
            </div>
          </div>
          <div className="hero__actions">
            <a href="#menu" className="button button--primary">
              대표 메뉴 보기
            </a>
            <a href="#visit" className="button button--ghost">
              방문 정보 보기
            </a>
          </div>
        </div>
        <div className="hero__media">
          <img src={profile.heroImage} alt="동그라미분식 매장 외관" />
        </div>
      </header>

      <main>
        <section id="menu" className="section">
          <div className="section-heading">
            <h2>대표 메뉴</h2>
          </div>
          <div className="product-grid">
            {menuItems.map((item) => (
              <article key={item.name} className="product-card">
                <div className="product-card__image">
                  <img src={item.image} alt={`${item.name} 메뉴 이미지`} />
                </div>
                <div className="product-card__body">
                  <span className="product-card__category">{item.category}</span>
                  <h3>{item.name}</h3>
                  <p>{item.description}</p>
                  <strong>{item.price}</strong>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section id="visit" className="section">
          <div className="section-heading">
            <h2>방문 정보</h2>
          </div>
          <div className="visit-grid">
            <article className="visit-card">
              <h3>영업시간</h3>
              <ul>
                {businessHours.map((slot) => (
                  <li key={slot.day}>
                    <span>{slot.day}</span>
                    <strong>{slot.hours}</strong>
                  </li>
                ))}
              </ul>
            </article>
            <article className="visit-card">
              <h3>매장 정보</h3>
              <dl>
                <div>
                  <dt>주소</dt>
                  <dd>{profile.location}</dd>
                </div>
                <div>
                  <dt>대표자</dt>
                  <dd>{profile.ceo}</dd>
                </div>
              </dl>
            </article>
          </div>
        </section>
      </main>

      <a
        href="#top"
        className={`floating-top-button${showTopButton ? " is-visible" : ""}`}
        aria-label="맨 위로 이동"
      >
        ⬆
      </a>
    </div>
  );
}

export default App;
