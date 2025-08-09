from flask import Flask, request, jsonify
from flask_cors import CORS
import json
import random
from datetime import datetime

app = Flask(__name__)
CORS(app)

# Simple in-memory storage
products_data = []
users_data = []
interactions_data = []

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "healthy", "message": "ML Service is running"})

@app.route('/data/load', methods=['POST'])
def load_data():
    try:
        data = request.get_json()
        global products_data, users_data, interactions_data
        
        products_data = data.get('products', [])
        users_data = data.get('users', [])
        interactions_data = data.get('interactions', [])
        
        return jsonify({
            "message": "Data loaded successfully",
            "products_count": len(products_data),
            "users_count": len(users_data),
            "interactions_count": len(interactions_data)
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/recommendations/guest', methods=['GET'])
def guest_recommendations():
    try:
        limit = int(request.args.get('limit', 10))
        
        if not products_data:
            return jsonify([])
        
        # Simple popularity-based recommendations
        popular_products = sorted(products_data, key=lambda x: x.get('views', 0), reverse=True)
        return jsonify(popular_products[:limit])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/recommendations/personalized/<int:user_id>', methods=['GET'])
def personalized_recommendations(user_id):
    try:
        limit = int(request.args.get('limit', 10))
        
        if not products_data:
            return jsonify([])
        
        # Simple personalized recommendations based on user interactions
        user_interactions = [i for i in interactions_data if i.get('user_id') == user_id]
        
        if not user_interactions:
            # If no interactions, return popular products
            popular_products = sorted(products_data, key=lambda x: x.get('views', 0), reverse=True)
            return jsonify(popular_products[:limit])
        
        # Get products the user has interacted with
        interacted_products = [i.get('product_id') for i in user_interactions]
        
        # Return products from same categories as interacted products
        recommended_products = []
        for product in products_data:
            if product.get('id') not in interacted_products:
                recommended_products.append(product)
        
        return jsonify(recommended_products[:limit])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/recommendations/similar/<int:product_id>', methods=['GET'])
def similar_products(product_id):
    try:
        limit = int(request.args.get('limit', 5))
        
        if not products_data:
            return jsonify([])
        
        # Find the target product
        target_product = None
        for product in products_data:
            if product.get('id') == product_id:
                target_product = product
                break
        
        if not target_product:
            return jsonify([])
        
        # Find products in the same category
        same_category = [p for p in products_data 
                        if p.get('category') == target_product.get('category') 
                        and p.get('id') != product_id]
        
        return jsonify(same_category[:limit])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/recommendations/trending', methods=['GET'])
def trending_products():
    try:
        limit = int(request.args.get('limit', 10))
        
        if not products_data:
            return jsonify([])
        
        # Simple trending based on views
        trending = sorted(products_data, key=lambda x: x.get('views', 0), reverse=True)
        return jsonify(trending[:limit])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/recommendations/category/<category>', methods=['GET'])
def category_recommendations(category):
    try:
        limit = int(request.args.get('limit', 10))
        
        if not products_data:
            return jsonify([])
        
        # Filter products by category
        category_products = [p for p in products_data if p.get('category') == category]
        return jsonify(category_products[:limit])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/interactions/record', methods=['POST'])
def record_interaction():
    try:
        interaction = request.get_json()
        interaction['timestamp'] = datetime.now().isoformat()
        interactions_data.append(interaction)
        
        return jsonify({"message": "Interaction recorded successfully"})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print("Starting ML Service on http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True) 