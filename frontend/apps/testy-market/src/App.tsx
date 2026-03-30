import interiorShelvesImage from "../assets/images/interior-shelves.png";
import marketTableImage from "../assets/images/market-table.png";
import shopCounterImage from "../assets/images/market-counter.png";
import storefrontImage from "../assets/images/storefront.png";
import tomatoesImage from "../assets/images/tomatoes.png";

const store = {
  name: "testy market",
  address: "505 Teheran-ro, Gangnam District, Seoul",
  ceo: "Jin-Soo Park"
};

const hours = [
  { day: "Monday", time: "Open 24 hours" },
  { day: "Tuesday", time: "Closed" },
  { day: "Wednesday", time: "12 PM - 12 AM" },
  { day: "Thursday", time: "12 PM - 10 AM" },
  { day: "Friday", time: "9 AM - 10 PM" },
  { day: "Saturday", time: "Closed" },
  { day: "Sunday", time: "Closed (Singmogil)" }
];

const principles = [
  {
    title: "Short distance food",
    text: "What arrives here should still feel close to the field, the greenhouse, or the kitchen that packed it."
  },
  {
    title: "Useful beauty",
    text: "The store is curated to feel warm, but every display is still meant to answer a simple question: what should I cook tonight?"
  },
  {
    title: "Neighborhood memory",
    text: "A local market should be more than a shelf. It should be where a routine starts to feel personal."
  }
];

const timeline = [
  {
    year: "Early Days",
    text: "testy market first began as a modest produce room, assembled with the plain and durable intention of keeping daily essentials visible, seasonal, and close at hand."
  },
  {
    year: "Now",
    text: "In time it has settled into a gentler sort of shop, where vegetables, pantry staples, flowers, and preserves are arranged with the calm confidence of a well-kept household."
  },
  {
    year: "Next",
    text: "Looking ahead, the market continues to widen its table with practical provisions that feel deliberately chosen, never merely placed upon a shelf by habit."
  }
];

const goods = [
  { name: "Heirloom Tomatoes", note: "Morning delivery", price: "₩5,000" },
  { name: "Farm Eggs", note: "Small-batch trays", price: "₩7,500" },
  { name: "Seasonal Citrus", note: "For table bowls and dressings", price: "₩6,000" },
  { name: "Local Honey", note: "Pantry shelf staple", price: "₩14,000" },
  { name: "Fresh Herbs", note: "Picked for quick cooking", price: "₩3,500" },
  { name: "Market Potatoes", note: "Roasting and soup stock", price: "₩4,800" }
];

function App() {
  return (
    <div className="market-page">
      <header className="market-hero">
        <img className="market-hero__background" src={storefrontImage} alt="testy market storefront" />
        <div className="market-hero__overlay" />
        <div className="market-hero__header">
          <p className="market-hero__eyebrow">Neighborhood produce archive</p>
          <div className="market-hero__title-row">
            <h1>{store.name}</h1>
          </div>
          <p className="market-hero__lede">
            A local market for everyday ingredients, slow browsing, and shelves that feel
            more like a handwritten recommendation than a catalog. testy market is designed
            as a neighborhood stop for produce, pantry goods, and simple food rituals.
          </p>
        </div>
      </header>

      <main className="market-layout">
        <section className="document-card document-card--summary">
          <div className="document-card__header">
            <span>Store Note</span>
            <h3 className="document-card__title">Why this store exists</h3>
          </div>
          <p>
            testy market is built around the kind of shopping that still feels human in a dense
            city. It values produce that looks alive, shelves that make sense at a glance, and a
            room where the customer can move from tomatoes to eggs to herbs without losing the
            feeling of place.
          </p>
          <p>
            The store does not try to feel grand. It tries to feel dependable, tactile, and local.
            Warm wood, handwritten labels, and compact displays are treated as part of the service,
            not decoration.
          </p>
          <div className="image-pair">
            <figure>
              <img src={interiorShelvesImage} alt="interior produce shelves" />
            </figure>
            <figure>
              <img src={marketTableImage} alt="produce table at testy market" />
            </figure>
          </div>
        </section>

        <aside className="document-card document-card--contact">
          <div className="document-card__header">
            <span>Business File</span>
            <h3 className="document-card__title">Location</h3>
          </div>
          <dl className="contact-list">
            <div>
              <dt>Address</dt>
              <dd>{store.address}</dd>
            </div>
          </dl>
          <div className="subsection">
            <h3 className="subsection__title">Store Hours</h3>
            <ul className="hours-table">
              {hours.map((slot) => (
                <li key={slot.day}>
                  <span>{slot.day}</span>
                  <strong>{slot.time}</strong>
                </li>
              ))}
            </ul>
          </div>
        </aside>

        <section className="document-card document-card--full">
          <div className="document-card__header">
            <span>Brand Principles</span>
            <h3 className="document-card__title">What defines the market</h3>
          </div>
          <div className="principle-list">
            {principles.map((item) => (
              <article key={item.title} className="principle-item">
                <h3>{item.title}</h3>
                <p>{item.text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="document-card document-card--full">
          <div className="document-card__header">
            <span>Selected Goods</span>
            <h3 className="document-card__title">Everyday goods worth taking home</h3>
          </div>
          <article className="featured-product">
            <figure className="featured-product__image">
              <img src={tomatoesImage} alt="heirloom tomatoes display" />
            </figure>
            <div className="featured-product__body">
              <p className="featured-product__eyebrow">Counter pick</p>
              <h3>Heirloom Tomatoes</h3>
              <strong>₩5,000</strong>
              <p>
                The most immediate expression of the shop: bright color, short distance, and a
                display that makes dinner planning feel easy.
              </p>
            </div>
          </article>
          <div className="goods-grid">
            {goods.map((item) => (
              <article key={item.name} className="goods-card">
                <p className="goods-card__name">{item.name}</p>
                <p className="goods-card__note">{item.note}</p>
                <strong>{item.price}</strong>
              </article>
            ))}
          </div>
        </section>

        <section className="document-card document-card--history">
          <div className="document-card__header">
            <span>Store History</span>
            <h3 className="document-card__title">How the shop took shape</h3>
          </div>
          <div className="history-layout">
            <div className="timeline-list">
              {timeline.map((item) => (
                <article key={item.year} className="timeline-item">
                  <span>{item.year}</span>
                  <p>{item.text}</p>
                </article>
              ))}
            </div>
            <figure className="document-photo">
              <img src={shopCounterImage} alt="market counter and produce display" />
            </figure>
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
